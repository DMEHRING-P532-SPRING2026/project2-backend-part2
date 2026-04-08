package iu.devinmehringer.project2.init;

import iu.devinmehringer.project2.access.order.OrderRepository;
import iu.devinmehringer.project2.access.staff.StaffRepository;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final StaffRepository staffRepository;
    private final OrderRepository orderRepository;

    public DataSeeder(StaffRepository staffRepository, OrderRepository orderRepository) {
        this.staffRepository = staffRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (staffRepository.count() == 0) {
            staffRepository.saveAll(List.of(
                    // Clinicians
                    new Staff("Dr. Smith", StaffType.CLINICIAN, null),
                    new Staff("Dr. Jones", StaffType.CLINICIAN, null),

                    // Imaging
                    new Staff("Jane Doe", StaffType.FULFILLMENT, Department.IMAGING),
                    new Staff("Carlos Rivera", StaffType.FULFILLMENT, Department.IMAGING),
                    new Staff("Priya Patel", StaffType.FULFILLMENT, Department.IMAGING),

                    // Lab
                    new Staff("John Smith", StaffType.FULFILLMENT, Department.LAB),
                    new Staff("Megan Torres", StaffType.FULFILLMENT, Department.LAB),
                    new Staff("Derek Huang", StaffType.FULFILLMENT, Department.LAB),

                    // Medication
                    new Staff("Jane Jill", StaffType.FULFILLMENT, Department.MEDICATION),
                    new Staff("Omar Hassan", StaffType.FULFILLMENT, Department.MEDICATION),
                    new Staff("Lisa Chen", StaffType.FULFILLMENT, Department.MEDICATION)
            ));
        }

        if (orderRepository.count() == 0) {
            Staff smith = staffRepository.getReferenceById(1L);
            Staff jones = staffRepository.getReferenceById(2L);

            orderRepository.saveAll(List.of(
                    // LAB
                    new Order(Department.LAB, "CBC", "Patient presenting with fatigue and pallor", Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(5), smith),
                    new Order(Department.LAB, "Lipid Panel", "Routine cholesterol screening", Priority.ROUTINE, Status.PENDING, LocalDateTime.now().minusMinutes(20), jones),
                    new Order(Department.LAB, "Blood Culture", "Suspected sepsis, fever 103F", Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(2), smith),
                    new Order(Department.LAB, "HbA1c", "Diabetic follow-up, glucose trending high", Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(45), jones),
                    new Order(Department.LAB, "Thyroid Panel", "Weight gain and fatigue symptoms", Priority.ROUTINE, Status.PENDING, LocalDateTime.now().minusHours(1), smith),
                    new Order(Department.LAB, "Troponin", "Chest pain, rule out MI", Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(8), jones),

                    // IMAGING
                    new Order(Department.IMAGING, "Chest X-Ray", "Persistent cough, rule out pneumonia", Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(15), smith),
                    new Order(Department.IMAGING, "CT Abdomen", "Acute abdominal pain, rule out appendicitis", Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(3), jones),
                    new Order(Department.IMAGING, "MRI Brain", "New onset seizures, headache", Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(30), smith),
                    new Order(Department.IMAGING, "Knee X-Ray", "Fall injury, knee swelling and pain", Priority.ROUTINE, Status.PENDING, LocalDateTime.now().minusHours(2), jones),
                    new Order(Department.IMAGING, "Echocardiogram", "Murmur detected on auscultation", Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(50), smith),
                    new Order(Department.IMAGING, "Ultrasound Pelvis", "Pelvic pain, rule out ovarian cyst", Priority.ROUTINE, Status.PENDING, LocalDateTime.now().minusHours(3), jones),

                    // MEDICATION
                    new Order(Department.MEDICATION, "Metoprolol 25mg", "Hypertension, BP 160/100", Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(10), smith),
                    new Order(Department.MEDICATION, "Amoxicillin 500mg", "Strep throat confirmed positive", Priority.ROUTINE, Status.PENDING, LocalDateTime.now().minusMinutes(25), jones),
                    new Order(Department.MEDICATION, "Morphine 4mg IV", "Post-op pain management", Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(1), smith),
                    new Order(Department.MEDICATION, "Ondansetron 4mg", "Nausea and vomiting post-chemo", Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(18), jones),
                    new Order(Department.MEDICATION, "Lisinopril 10mg", "Follow-up HTN management", Priority.ROUTINE, Status.PENDING, LocalDateTime.now().minusHours(1).minusMinutes(30), smith),
                    new Order(Department.MEDICATION, "Insulin Glargine 20u", "T1DM, fasting glucose 280", Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(40), jones)
            ));
        }
    }
}