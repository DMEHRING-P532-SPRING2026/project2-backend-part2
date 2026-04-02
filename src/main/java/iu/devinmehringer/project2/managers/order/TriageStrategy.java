package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.model.order.Order;

import java.util.List;

public interface TriageStrategy {
    public List<Order> getSortedOrders();
}
