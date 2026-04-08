package iu.devinmehringer.project2.access.staff;

import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffAccess {
        private final StaffRepository staffRepository;

    public StaffAccess(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public Staff saveStaff(Staff staff) {
        staffRepository.save(staff);
        return staff;
    }

    public Staff getStaffFromID(long id) {
        return this.staffRepository.findById(id).orElse(null);
    }

    public List<Staff> getStaffByDepartment(Department department) {
        return this.staffRepository.findByTypeAndDepartment(StaffType.FULFILLMENT, department);
    }

    public List<Staff> getClinicians() {
        return this.staffRepository.findByType(StaffType.CLINICIAN);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }
}
