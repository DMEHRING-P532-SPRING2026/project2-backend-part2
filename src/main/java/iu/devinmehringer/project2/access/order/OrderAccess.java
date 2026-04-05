package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.order.Type;
import org.aspectj.weaver.ast.Or;
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

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderExceptions.OrderNotFoundException(id));
    }

    public List<Order> getPendingOrdersByType(Type type) {
        return orderRepository.findByTypeOrderByCreatedAtAsc(type);
    }

    public List<Order> getPendingOrdersByPriorityAndType(Priority priority, Type type) {
        return orderRepository.findByStatusAndPriorityAndTypeOrderByCreatedAtAsc(Status.PENDING, priority, type);
    }

    public List<Order> getPendingOrdersByDeadlineAndType(Type type) {
        return orderRepository.findByTypeOrderByDeadlineAsc(type);
    }

    public List<Order> getRecentStatOrders(Type type, LocalDateTime since) {
        return orderRepository.findByCreatedAtAfterAndPriorityAndType(since, Priority.STAT, type);
    }

}
