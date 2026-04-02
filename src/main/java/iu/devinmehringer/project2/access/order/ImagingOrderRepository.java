package iu.devinmehringer.project2.access.order;

import iu.devinmehringer.project2.model.order.ImagingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagingOrderRepository extends JpaRepository<ImagingOrder, Long> {
    // TODO Add all needed queries
}
