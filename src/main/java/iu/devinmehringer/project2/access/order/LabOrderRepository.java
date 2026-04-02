package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.model.order.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {
    // TODO Add all needed queries
}
