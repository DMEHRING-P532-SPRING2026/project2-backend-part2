package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.command.CommandAccess;
import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.model.command.CommandRecord;
import iu.devinmehringer.project2.model.command.Type;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.utilities.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderManager implements Subject {

    private final OrderFactory orderFactory;
    private final OrderAccess orderAccess;
    private final CommandAccess commandAccess;
    private final Handler commandPipeline;
    private final TriagingEngine triagingEngine;
    private final List<NotificationService> notifiers;

    public OrderManager(OrderFactory orderFactory, OrderAccess orderAccess,
                        CommandAccess commandAccess, TriagingEngine triagingEngine,
                        List<NotificationService> notifiers) {
        this.orderFactory = orderFactory;
        this.orderAccess = orderAccess;
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

    /**
     * Base Handler - execute the given command
     */
    private static class BaseHandler implements Handler {
        @Override
        public void handle(OrderCommand command) {
            command.execute();
        }
    }

    /**
     * Notification Handler - used to notifier the observers
     */
    private class NotificationHandler extends HandlerDecorator {

        public NotificationHandler(Handler handler) {
            super(handler);
        }

        @Override
        public void handle(OrderCommand command) {
            super.handle(command);
            notifyObservers(command, command.getEvent(), command.getPreferences());
        }
    }

    /**
     * Command Logger Handler - saves the commands for reference
     */
    private class CommandLoggerHandler extends HandlerDecorator {

        public CommandLoggerHandler(Handler handler) {
            super(handler);
        }

        @Override
        public void handle(OrderCommand command) {
            super.handle(command);
            Order order = command.getOrder();
            String actor = command.getType().equals(Type.CREATE) ? order.getClinician() : command.getActor();
            String other = order.getPriority() == Priority.STAT
                    ? String.format("STAT AUDIT | Patient: %s | Type: %s", order.getPatient(), order.getType())
                    : null;

            commandAccess.saveCommand(new CommandRecord(command.getType(), order.getId(), actor, other));
        }
    }

    /**
     * Escalate Orders
     */
    private class PriorityEscalationHandler extends HandlerDecorator {

        public PriorityEscalationHandler(Handler handler) {
            super(handler);
        }

        @Override
        public void handle(OrderCommand command) {
            super.handle(command);

            if (!command.getType().equals(Type.CREATE)) return;

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



    /**
     * Validation Handler - validate request
     */
    private class ValidationHandler extends HandlerDecorator {

        public ValidationHandler(Handler handler) {
            super(handler);
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
            if (!command.getOrder().getStatus().equals(Status.PENDING)) {
                throw new OrderExceptions.OrderCancelException(command.getId());
            }
        }

        private void submitCheck(OrderCommand command) {
            Order order = orderAccess.getOrderById(command.getId());
            command.setOrder(order);
            command.setId(order.getId());
            if (!command.getOrder().getStatus().equals(Status.IN_PROGRESS)) {
                throw new OrderExceptions.OrderSubmitException(command.getId());
            }
            // If you're not the actor we have on the order you can't submit it
            if (!command.getActor().equals(order.getCurrentActor())) {
                throw new OrderExceptions.OrderActorException(command.getId());
            }
        }

        @Override
        public void handle(OrderCommand command) {
            switch (command.getType()) {
                case CANCEL -> cancelCheck(command);
                case CLAIM -> claimCheck(command);
                case SUBMIT -> submitCheck(command);
            }
            super.handle(command);
        }
    }

    /**
     * Create Order command
     * @param orderRequest data from controller about order
     * @return Order
     */
    public Order createOrder(OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                this.order = orderFactory.create(
                        orderRequest.getType(),
                        orderRequest.getPatient(),
                        orderRequest.getClinician(),
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
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Create Order");
        command.setActor(orderRequest.getActor());
        command.setType(Type.CREATE);
        commandPipeline.handle(command);
        return command.getOrder();
    }

    /**
     * Claim order command
     * @param id the id of the order
     * @param orderRequest controller request
     * @return Order
     */
    public Order claimOrder(Long id, OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                this.order.setCurrentActor(orderRequest.getActor());
                this.order.setStatus(Status.IN_PROGRESS);
                order.setLastModifiedAt(LocalDateTime.now());
            }
        };
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Claim Order");
        command.setActor(orderRequest.getActor());
        command.setType(Type.CLAIM);
        command.setId(id);
        commandPipeline.handle(command);
        return command.getOrder();
    }

    /**
     * Cancel order command
     * @param id the id of the order
     * @param orderRequest controller request
     * @return Order
     */
    public Order cancelOrder(Long id, OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                this.order.setStatus(Status.CANCELLED);
                order.setLastModifiedAt(LocalDateTime.now());
            }
        };
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Cancel Order");
        command.setActor(orderRequest.getActor());
        command.setType(Type.CANCEL);
        command.setId(id);
        commandPipeline.handle(command);
        return command.getOrder();
    }

    /**
     * Submit order command
     * @param id the id of the order
     * @param orderRequest controller request
     * @return Order
     */
    public Order submitOrder(Long id, OrderRequest orderRequest) {
        OrderCommand command = new OrderCommand() {
            @Override
            public void execute() {
                this.order.setStatus(Status.COMPLETED);
                order.setLastModifiedAt(LocalDateTime.now());
            }
        };
        command.setPreferences(orderRequest.getPreferences());
        command.setEvent("Submit Order");
        command.setActor(orderRequest.getActor());
        command.setType(Type.SUBMIT);
        command.setId(id);
        commandPipeline.handle(command);
        return command.getOrder();
    }

    public List<Order> getPendingOrders(TriageStrategyType triageStrategy,
                                        iu.devinmehringer.project2.model.order.Type type) {
        return triagingEngine.getPending(triageStrategy, type);
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
