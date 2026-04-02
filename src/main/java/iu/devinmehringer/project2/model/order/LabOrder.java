package iu.devinmehringer.project2.model.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="lab_orders")
public class LabOrder extends Order {

    protected LabOrder() {}

    public LabOrder(String patient, String clinician, String description, Priority priority) {
        super(Type.LAB, patient, clinician, description, priority, Status.PENDING, LocalDateTime.now());
    }
}
