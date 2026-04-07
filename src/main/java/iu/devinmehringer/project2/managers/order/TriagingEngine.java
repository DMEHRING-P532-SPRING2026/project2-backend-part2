package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.OrderType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TriagingEngine {
    private final OrderAccess orderAccess;

    public TriagingEngine(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    public List<Order> getPending(TriageStrategyType strategyType, OrderType orderType) {
        switch (strategyType) {
            case LOAD_BALANCING -> {
                return new LoadBalancing().getSortedOrders(orderType);
            }
            case DEADLINE_FIRST -> {
                return new DeadlineFirst().getSortedOrders(orderType);
            }
            case PRIORITY_FIRST -> {
                return new PriorityFirst().getSortedOrders(orderType);
            }
        }
        return new ArrayList<Order>();
    }

    private class PriorityFirst implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(OrderType orderType) {
            List<Order> orders = new ArrayList<>();
            for (Priority priority : Priority.values()) {
                orders.addAll(orderAccess.getPendingOrdersByPriorityAndType(priority, orderType));
            }
            return orders;
        }
    }

    private class LoadBalancing implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(OrderType orderType) {
            return orderAccess.getPendingOrdersByType(orderType);
        }
    }

    private class DeadlineFirst implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(OrderType orderType) {
            return orderAccess.getPendingOrdersByDeadlineAndType(orderType);
        }
    }
}
