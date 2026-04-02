package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TriagingEngine {
    private final OrderAccess orderAccess;

    public TriagingEngine(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    public class PriorityFirst implements TriageStrategy {
        @Override
        public List<Order> getSortedOrders() {
            List<Order> orders = new ArrayList<>();
            for (Priority priority : Priority.values()) {
                orders.addAll(orderAccess.getPendingOrdersByPriority(priority));
            }
            return orders;
        }
    }

    public TriageStrategy priorityFirst() { return new PriorityFirst(); }
}
