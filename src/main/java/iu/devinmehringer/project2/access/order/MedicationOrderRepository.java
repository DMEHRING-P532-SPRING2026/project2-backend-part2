package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.model.order.MedicationOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationOrderRepository extends JpaRepository<MedicationOrder, Long> {
    // TODO Add all needed queries
}
