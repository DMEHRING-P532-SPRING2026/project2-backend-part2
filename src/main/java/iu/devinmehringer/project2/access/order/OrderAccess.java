package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.order.Type;
import org.springframework.stereotype.Service;

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

    public List<Order> getPendingOrdersByPriority(Priority priority) {
        return orderRepository.findByStatusAndPriorityOrderByCreatedAtAsc(Status.PENDING, priority);
    }
}
