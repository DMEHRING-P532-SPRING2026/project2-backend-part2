package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TriagingEngine {
    private final OrderAccess orderAccess;
    private final StaffAccess staffAccess;

    public TriagingEngine(OrderAccess orderAccess, StaffAccess staffAccess) {
        this.orderAccess = orderAccess;
        this.staffAccess = staffAccess;
    }

    public List<Order> getPending(Staff staff, TriageStrategyType strategyType, Department department) {
        switch (strategyType) {
            case LOAD_BALANCING -> {
                return new LoadBalancing().getSortedOrders(department, staff);
            }
            case DEADLINE_FIRST -> {
                return new DeadlineFirst().getSortedOrders(department, staff);
            }
            case PRIORITY_FIRST -> {
                return new PriorityFirst().getSortedOrders(department, staff);
            }
        }
        return new ArrayList<Order>();
    }

    private class PriorityFirst implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(Department department,Staff staff) {
            List<Order> orders = new ArrayList<>();
            for (Priority priority : Priority.values()) {
                orders.addAll(orderAccess.getPendingOrdersByPriorityAndDepartment(priority, department));
            }
            return orders;
        }
    }

    private class LoadBalancing implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(Department department, Staff staff) {
            List<Order> pending = orderAccess.getPendingOrdersByDepartment(department);

            long myCount = orderAccess.getByStaffContainingAndDepartmentAndStatus(staff, department, Status.IN_PROGRESS).size();

            boolean othersHaveLess = staffAccess.getStaffByDepartment(department).stream()
                    .filter(s -> !s.getId().equals(staff.getId()))
                    .anyMatch(s -> orderAccess.getByStaffContainingAndDepartmentAndStatus(s, department, Status.IN_PROGRESS).size() < myCount);

            if (othersHaveLess) return List.of();

            return pending;
        }
    }

    private class DeadlineFirst implements TriageStrategy {

        @Override
        public List<Order> getSortedOrders(Department department, Staff staff) {
            return orderAccess.getPendingOrdersByDepartment(department);
        }
    }
}
