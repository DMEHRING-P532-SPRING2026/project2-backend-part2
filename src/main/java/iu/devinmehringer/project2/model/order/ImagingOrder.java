package iu.devinmehringer.project2.model.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="imaging_orders")
public class ImagingOrder extends Order {

    protected ImagingOrder() {}

    public ImagingOrder(String patient, String clinician, String description, Priority priority) {
        super(Type.IMAGING, patient, clinician, description, priority, Status.PENDING, LocalDateTime.now());
    }
}
