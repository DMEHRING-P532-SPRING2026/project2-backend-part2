package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.access.staff.StaffRepository;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final StaffRepository staffRepository;

    public DataSeeder(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
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
    }
}