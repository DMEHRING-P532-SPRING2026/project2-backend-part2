package iu.devinmehringer.project2.model.order;

import iu.devinmehringer.project2.model.staff.Staff;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="lab_orders")
public class LabOrder extends Order {

    protected LabOrder() {}

    public LabOrder(String patient, Staff staff, String description, Priority priority) {
        super(Department.LAB, patient, description, priority, Status.PENDING, LocalDateTime.now(), staff);
    }
}
