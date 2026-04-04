package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Type;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TriagingEngine {
    private final OrderAccess orderAccess;

    public TriagingEngine(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    public List<Order> getPending(TriageStrategyType strategyType, Type type) {
        switch (strategyType) {
            case LOAD_BALANCING -> {
                return new LoadBalancing().getSortedOrders(type);
            }
            case DEADLINE_FIRST -> {
                return new DeadlineFirst().getSortedOrders(type);
            }
            case PRIORITY_FIRST -> {
                return new PriorityFirst().getSortedOrders(type);
            }
        }
        return new ArrayList<Order>();
    }

    private class PriorityFirst implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(Type type) {
            List<Order> orders = new ArrayList<>();
            for (Priority priority : Priority.values()) {
                orders.addAll(orderAccess.getPendingOrdersByPriorityAndType(priority, type));
            }
            return orders;
        }
    }

    private class LoadBalancing implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(Type type) {
            return orderAccess.getPendingOrdersByType(type);
        }
    }

    private class DeadlineFirst implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(Type type) {
            return orderAccess.getPendingOrdersByDeadlineAndType(type);
        }
    }
}
