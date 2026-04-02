package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.model.order.*;
import org.springframework.stereotype.Service;

@Service
public class OrderFactory {
    public Order create(Type type, String patient, String clinician, String description, Priority priority) {
        return switch (type) {
            case LAB -> new LabOrder(patient, clinician, description, priority);
            case MEDICATION -> new MedicationOrder(patient, clinician, description, priority);
            case IMAGING -> new ImagingOrder(patient, clinician, description, priority);
        };
    }
}
