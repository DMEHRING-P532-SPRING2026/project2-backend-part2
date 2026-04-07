package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.order.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderTypeOrderByCreatedAtAsc(OrderType orderType);
    List<Order> findByStatusAndPriorityAndOrderTypeOrderByCreatedAtAsc(Status status, Priority priority, OrderType orderType);
    List<Order> findByOrderTypeOrderByDeadlineAsc(OrderType orderType);
    List<Order> findByCreatedAtAfterAndPriorityAndOrderType(LocalDateTime since, Priority priority, OrderType orderType);
}
