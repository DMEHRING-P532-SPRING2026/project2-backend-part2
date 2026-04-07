package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.order.OrderType;
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

    public List<Order> getPendingOrdersByType(OrderType orderType) {
        return orderRepository.findByOrderTypeOrderByCreatedAtAsc(orderType);
    }

    public List<Order> getPendingOrdersByPriorityAndType(Priority priority, OrderType orderType) {
        return orderRepository.findByStatusAndPriorityAndOrderTypeOrderByCreatedAtAsc(Status.PENDING, priority, orderType);
    }

    public List<Order> getPendingOrdersByDeadlineAndType(OrderType orderType) {
        return orderRepository.findByOrderTypeOrderByDeadlineAsc(orderType);
    }

    public List<Order> getRecentStatOrders(OrderType orderType, LocalDateTime since) {
        return orderRepository.findByCreatedAtAfterAndPriorityAndOrderType(since, Priority.STAT, orderType);
    }

}
