package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDepartmentOrderByCreatedAtAsc(Department department);
    List<Order> findByStatusAndPriorityAndDepartmentOrderByCreatedAtAsc(Status status, Priority priority, Department department);
    List<Order> findByDepartmentOrderByDeadlineAsc(Department department);
    List<Order> findByStatusAndDepartmentOrderByCreatedAtAsc(Status status, Department department);
    List<Order> findByCreatedAtAfterAndPriorityAndDepartment(LocalDateTime since, Priority priority, Department department);
    List<Order> findByStaffContainingAndDepartmentAndStatus(Staff staff, Department department, Status status);
    List<Order> findByStaffContaining(Staff staff);
}