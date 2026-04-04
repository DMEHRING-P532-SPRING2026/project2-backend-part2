package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.order.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTypeOrderByCreatedAtAsc(Type type);
    List<Order> findByStatusAndPriorityAndTypeOrderByCreatedAtAsc(Status status, Priority priority, Type type);
    List<Order> findByTypeOrderByDeadlineAsc(Type type);
}
