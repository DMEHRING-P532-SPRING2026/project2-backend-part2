package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.command.CommandAccess;
import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.model.command.CommandRecord;
import iu.devinmehringer.project2.model.command.CommandType;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import iu.devinmehringer.project2.utilities.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class OrderManager implements Subject {

    private final OrderFactory orderFactory;
    private final OrderAccess orderAccess;
    private final StaffAccess staffAccess;
    private final CommandAccess commandAccess;
    private final Handler commandPipeline;
    private final TriagingEngine triagingEngine;
    private final List<NotificationService> notifiers;

    private OrderCommand lastCommand;

    public OrderManager(OrderFactory orderFactory, OrderAccess orderAccess, StaffAccess staffAccess,
                        CommandAccess commandAccess, TriagingEngine triagingEngine,
                        List<NotificationService> notifiers) {
        this.orderFactory = orderFactory;
        this.orderAccess = orderAccess;
        this.staffAccess = staffAccess;
        this.commandAccess = commandAccess;
        this.triagingEngine = triagingEngine;
        this.notifiers = notifiers;
        commandPipeline = new ValidationHandler(
                new PriorityEscalationHandler(
                        new NotificationHandler(
                                new CommandLoggerHandler(
                                        new BaseHandler()
                                ))));
    }

    private static class BaseHandler implements Handler {
        @Override
        public void handle(OrderCommand command) {
            command.execute();
        }
    }

    private class NotificationHandler extends HandlerDecorator {
        public NotificationHandler(Handler handler) { super(handler); }

        @Override
        public void handle(OrderCommand command) {
            super.handle(command);
            notifyObservers(command, command.getEvent(), command.getPreferences());
        }
    }

    private class CommandLoggerHandler extends HandlerDecorator {
        public CommandLoggerHandler(Handler handler) { super(handler); }

        @Override
        public void handle(OrderCommand command) {
            super.handle(command);
            Order order = command.getOrder();
            String other = order.getPriority() == Priority.STAT
                    ? String.format("STAT AUDIT | Patient: %s | CommandType: %s", order.getPatient(), order.getType())
                    : String.format("patient=%s|description=%s|department=%s|priority=%s",
                    order.getPatient(), order.getDescription(), order.getDepartment(), order.getPriority());
            commandAccess.saveCommand(new CommandRecord(command.getType(), order.getId(), command.getStaff(), other));
        }
    }

    private class PriorityEscalationHandler extends HandlerDecorator {
        public PriorityEscalationHandler(Handler handler) { super(handler); }

        @Override
        public void handle(OrderCommand command) {
            super.handle(command);
            if (!command.getType().equals(CommandType.CREATE)) return;
            Order order = command.getOrder();
            if (order.getPriority() != Priority.URGENT) return;
            List<Order> recentStats = orderAccess.getRecentStatOrders(order.getType(),
                    LocalDateTime.now().minusMinutes(5));
            if (!recentStats.isEmpty()) {
                order.setPriority(Priority.STAT);
                order.setDeadline(LocalDateTime.now().plusMinutes(30));
                orderAccess.saveOrder(order);
            }
        }
    }

    private class ValidationHandler extends HandlerDecorator {
        public ValidationHandler(Handler handler) { super(handler); }

        private void validateRequester(List<Staff> orderStaff, Staff requester) {
            if (!orderStaff.contains(requester)) {
                throw new OrderExceptions.OrderStaffNotSameAsRequesterException(requester.getId());
            }
        }

        private void claimCheck(OrderCommand command) {
            Order order = orderAccess.getOrderById(command.getId());
            command.setOrder(order);
            command.setId(order.getId());
            if (!command.getOrder().getStatus().equals(Status.PENDING)) {
                throw new OrderExceptions.OrderClaimException(command.getId());
            }
        }

        private void cancelCheck(OrderCommand command) {
            Order order = orderAccess.getOrderById(command.getId());
            command.setOrder(order);
            command.setId(order.getId());
            if (!command.getStaff().getType().equals(StaffType.CLINICIAN)) {
                throw new OrderExceptions.NonOwnerClinicianCancelOrderException(command.getStaffId());
            }
        }

        private void submitCheck(OrderCommand command) {
            Order order = orderAccess.getOrderById(command.getId());
            command.setOrder(order);
            command.setId(order.getId());
            validateRequester(order.getStaff(), command.getStaff());
            if (!command.getOrder().getStatus().equals(Status.IN_PROGRESS)) {
                throw new OrderExceptions.OrderSubmitException(command.getId());
            }
        }

        private void createCheck(OrderCommand command) {
            if (!command.getStaff().getType().equals(StaffType.CLINICIAN)) {
                throw new OrderExceptions.NonClinicianCreateOrderException(command.getStaffId());
            }
        }

        @Override
        public void handle(OrderCommand command) {
            Staff staff = staffAccess.getStaffFromID(command.getStaffId());
            if (staff != null) {
                command.setStaff(staff);
            } else {
                throw new OrderExceptions.UnknownStaffException(command.getStaffId());
            }
            switch (command.getType()) {
                case CANCEL -> cancelCheck(command);
                case CLAIM -> claimCheck(command);
                case SUBMIT -> submitCheck(command);
                case CREATE -> createCheck(command);
            }
            command.savePreviousState();
            super.handle(command);
        }
    }

    public Order createOrder(OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                Staff staff = staffAccess.getStaffFromID(orderRequest.getStaffId());
                this.order = orderFactory.create(
                        orderRequest.getType(),
                        orderRequest.getPatient(),
                        staff,
                        orderRequest.getDescription(),
                        orderRequest.getPriority()
                );
                switch (this.order.getPriority()) {
                    case STAT -> this.order.setDeadline(LocalDateTime.now().plusMinutes(30));
                    case URGENT -> this.order.setDeadline(LocalDateTime.now().plusHours(5));
                    case ROUTINE -> this.order.setDeadline(LocalDateTime.now().plusDays(7));
                }
                order.setLastModifiedAt(LocalDateTime.now());
                orderAccess.saveOrder(order);
            }
        };
        command.setStaffId(orderRequest.getStaffId());
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Create Order");
        command.setType(CommandType.CREATE);
        commandPipeline.handle(command);
        lastCommand = command;
        return command.getOrder();
    }

    public Order claimOrder(Long id, OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                Staff staff = staffAccess.getStaffFromID(orderRequest.getStaffId());
                this.order.addStaff(staff);
                this.order.setStatus(Status.IN_PROGRESS);
                order.setLastModifiedAt(LocalDateTime.now());
                orderAccess.saveOrder(order);
            }
        };
        command.setStaffId(orderRequest.getStaffId());
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Claim Order");
        command.setType(CommandType.CLAIM);
        command.setId(id);
        commandPipeline.handle(command);
        lastCommand = command;
        return command.getOrder();
    }

    public Order cancelOrder(Long id, OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                this.order.setStatus(Status.CANCELLED);
                order.setLastModifiedAt(LocalDateTime.now());
                orderAccess.saveOrder(order);
            }
        };
        command.setStaffId(orderRequest.getStaffId());
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Cancel Order");
        command.setType(CommandType.CANCEL);
        command.setId(id);
        commandPipeline.handle(command);
        lastCommand = command;
        return command.getOrder();
    }

    public Order submitOrder(Long id, OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                this.order.setStatus(Status.COMPLETED);
                order.setLastModifiedAt(LocalDateTime.now());
                orderAccess.saveOrder(order);
            }
        };
        command.setStaffId(orderRequest.getStaffId());
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Submit Order");
        command.setType(CommandType.SUBMIT);
        command.setId(id);
        commandPipeline.handle(command);
        lastCommand = command;
        return command.getOrder();
    }

    /**
     * Undo the last executed command by restoring the previous state snapshot
     */
    public Order undoLastCommand() {
        if (lastCommand == null) {
            throw new RuntimeException("No command to undo");
        }

        Order order = lastCommand.getOrder();
        if (lastCommand.getType() == CommandType.CREATE) {
            order.setStatus(Status.CANCELLED);
            order.setLastModifiedAt(LocalDateTime.now());
            orderAccess.saveOrder(order);
            commandAccess.saveCommand(new CommandRecord(CommandType.UNDO, order.getId(), lastCommand.getStaff(), "Undo CREATE - order cancelled"));
            lastCommand = null;
            return order;
        }

        order.setStatus(lastCommand.getPreviousStatus());
        order.setStaff(lastCommand.getPreviousStaff());
        order.setPriority(lastCommand.getPreviousPriority());
        order.setDeadline(lastCommand.getPreviousDeadline());
        order.setLastModifiedAt(LocalDateTime.now());
        orderAccess.saveOrder(order);

        commandAccess.saveCommand(new CommandRecord(CommandType.UNDO, order.getId(), lastCommand.getStaff(),
                "Undo " + lastCommand.getType() + " - reverted to " + lastCommand.getPreviousStatus()));
        lastCommand = null;
        return order;
    }

    public Order replayCommand(Long commandRecordId, OrderRequest orderRequest) {
        CommandRecord record = commandAccess.getCommandById(commandRecordId);
        return switch (record.getType()) {
            case CLAIM -> claimOrder(record.getOrderId(), orderRequest);
            case CANCEL -> cancelOrder(record.getOrderId(), orderRequest);
            case SUBMIT -> submitOrder(record.getOrderId(), orderRequest);
            case CREATE -> {
                Order original = orderAccess.getOrderById(record.getOrderId());
                OrderRequest createRequest = new OrderRequest();
                createRequest.setStaffId(record.getStaff().getId());
                createRequest.setType(original.getDepartment());
                createRequest.setPatient(original.getPatient());
                createRequest.setDescription(original.getDescription());
                createRequest.setPriority(original.getPriority());
                yield createOrder(createRequest);
            }
            case UNDO -> throw new RuntimeException("Cannot replay an UNDO command");
        };
    }

    public List<Order> getPendingOrders(Long staffId, TriageStrategyType triageStrategy,
                                        Department department) {
        Staff staff = this.staffAccess.getStaffFromID(staffId);
        return triagingEngine.getPending(staff, triageStrategy, department);
    }

    public List<Order> getClaimedOrders(Long staffId, Department department) {
        Staff staff = staffAccess.getStaffFromID(staffId);
        return orderAccess.getByStaffContainingAndDepartmentAndStatus(staff, department, Status.IN_PROGRESS);
    }

    public List<Order> getOrdersByClinician(Long staffId) {
        Staff staff = staffAccess.getStaffFromID(staffId);
        return orderAccess.findActiveOrdersByClinician(staff);
    }

    public List<CommandRecord> getOrderCommands() {
        return commandAccess.getCommands();
    }

    public Order getOrderById(Long id) {
        return orderAccess.getOrderById(id);
    }

    @Override
    public void addObserver(Observer observer) {}

    @Override
    public void removeObserver(Observer observer) {}

    @Override
    public void notifyObservers(OrderCommand command, String event, NotificationPreferences preferences) {
        if (preferences == null) return;
        notifiers.stream()
                .filter(n -> preferences.isEnabled(n.getClass()))
                .forEach(n -> n.update(command, event));
    }
}