package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.staff.Staff;

import java.util.List;

public interface TriageStrategy {
    public List<Order> getSortedOrders(Department department, Staff staff);
}
