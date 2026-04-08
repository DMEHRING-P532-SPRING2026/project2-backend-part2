package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.model.order.*;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.stereotype.Service;

@Service
public class OrderFactory {
    public Order create(Department department, String patient, Staff staff, String description, Priority priority) {
        return switch (department) {
            case LAB -> new LabOrder(patient, staff, description, priority);
            case MEDICATION -> new MedicationOrder(patient, staff, description, priority);
            case IMAGING -> new ImagingOrder(patient, staff, description, priority);
        };
    }
}
