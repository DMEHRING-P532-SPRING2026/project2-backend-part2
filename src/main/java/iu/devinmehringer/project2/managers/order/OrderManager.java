package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.command.CommandAccess;
import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.model.command.CommandRecord;
import iu.devinmehringer.project2.model.command.Type;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.utilities.NotificationService;
import iu.devinmehringer.project2.utilities.Observer;
import iu.devinmehringer.project2.utilities.Subject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class OrderManager implements Subject {

    private final OrderFactory orderFactory;
    private final OrderAccess orderAccess;
    private final CommandAccess commandAccess;
    private final List<Observer> observers;
    private final Handler commandPipeline;
    private final TriagingEngine triagingEngine;
    private final TriageStrategy triageStrategy;

    public OrderManager(OrderFactory orderFactory, OrderAccess orderAccess, NotificationService notificationService,
                        CommandAccess commandAccess, TriagingEngine triagingEngine) {
        this.orderFactory = orderFactory;
        this.orderAccess = orderAccess;
        this.commandAccess = commandAccess;
        this.triagingEngine = triagingEngine;
        this.triageStrategy = triagingEngine.priorityFirst(); // default
        observers = new ArrayList<>();
        addObserver(notificationService);
        commandPipeline = new ValidationHandler(
                            new NotificationHandler(
                                new CommandLoggerHandler(
                                    new BaseHandler()
                        )));
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
            notifyObservers(command, command.getEvent());
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
            if (command.getType().equals(Type.CREATE)) {
                commandAccess.saveCommand(new CommandRecord(command.getType(), command.getOrder().getId(),
                        command.getOrder().getClinician()));
            } else {
                commandAccess.saveCommand(new CommandRecord(command.getType(), command.getOrder().getId(),
                        command.getActor()));
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
                order.setLastModifiedAt(LocalDateTime.now());
                orderAccess.saveOrder(order);
            }
        };
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
        command.setEvent("Submit Order");
        command.setActor(orderRequest.getActor());
        command.setType(Type.SUBMIT);
        command.setId(id);
        commandPipeline.handle(command);
        return command.getOrder();
    }

    public List<Order> getPendingOrders() {
        return triageStrategy.getSortedOrders();
    }

    public List<Order> getPendingOrders(iu.devinmehringer.project2.model.order.Type type) {
        return triageStrategy.getSortedOrders(type);
    }

    public List<CommandRecord> getOrderCommands() {
        return commandAccess.getCommands();
    }

    public Order getOrderById(Long id) {
        return orderAccess.getOrderById(id);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(OrderCommand command, String event) {
        observers.forEach(observer -> observer.update(command, event));
    }

}
