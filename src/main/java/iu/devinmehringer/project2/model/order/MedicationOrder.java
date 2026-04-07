package iu.devinmehringer.project2.model.order;

import iu.devinmehringer.project2.model.staff.Staff;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="medication_orders")
public class MedicationOrder extends Order {

    public MedicationOrder() {}

    public MedicationOrder(String patient, Staff staff, String description, Priority priority) {
        super(OrderType.MEDICATION, patient, description, priority, Status.PENDING, LocalDateTime.now(), staff);
    }
}
