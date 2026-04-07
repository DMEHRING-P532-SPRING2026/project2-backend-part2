package iu.devinmehringer.project2.model.order;

import iu.devinmehringer.project2.model.staff.Staff;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="imaging_orders")
public class ImagingOrder extends Order {

    protected ImagingOrder() {}

    public ImagingOrder(String patient, Staff staff, String description, Priority priority) {
        super(OrderType.IMAGING, patient, description, priority, Status.PENDING, LocalDateTime.now(), staff);
    }
}
