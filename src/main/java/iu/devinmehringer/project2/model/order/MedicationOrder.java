package iu.devinmehringer.project2.model.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="medication_orders")
public class MedicationOrder extends Order {

    public MedicationOrder() {}

    public MedicationOrder(String patient, String clinician, String description, Priority priority) {
        super(Type.MEDICATION, patient, clinician, description, priority, Status.PENDING, LocalDateTime.now());
    }
}
