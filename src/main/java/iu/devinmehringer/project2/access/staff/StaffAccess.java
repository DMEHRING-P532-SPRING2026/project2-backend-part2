package iu.devinmehringer.project2.access.staff;

import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.stereotype.Service;

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
}
