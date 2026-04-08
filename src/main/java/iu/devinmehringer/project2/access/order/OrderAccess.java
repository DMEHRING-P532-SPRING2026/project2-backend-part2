package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderAccess {

    public OrderRepository orderRepository;

    public OrderAccess(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order saveOrder(Order order) {
        orderRepository.save(order);
        return order;
    }

    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderExceptions.OrderNotFoundException(id));
    }

    public List<Order> getPendingOrdersByDepartment(Department department) {
        return orderRepository.findByStatusAndDepartmentOrderByCreatedAtAsc(Status.PENDING, department);
    }

    public List<Order> getPendingOrdersByPriorityAndDepartment(Priority priority, Department department) {
        return orderRepository.findByStatusAndPriorityAndDepartmentOrderByCreatedAtAsc(Status.PENDING, priority, department);
    }

    public List<Order> getRecentStatOrders(Department department, LocalDateTime since) {
        return orderRepository.findByCreatedAtAfterAndPriorityAndDepartment(since, Priority.STAT, department);
    }

    public List<Order> getByStaffContainingAndDepartmentAndStatus(Staff staff, Department department, Status status) {
        return orderRepository.findByStaffContainingAndDepartmentAndStatus(staff, department, status);
    }

    public List<Order> findActiveOrdersByClinician(Staff staff) {
        return orderRepository.findByStaffContaining(staff).stream()
                .filter(o -> o.getStatus() == Status.PENDING || o.getStatus() == Status.IN_PROGRESS)
                .toList();
    }
}