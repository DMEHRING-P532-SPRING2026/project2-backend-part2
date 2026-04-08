package iu.devinmehringer.project2.managers.staff;

import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.controller.dto.StaffRequest;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffManager {
    private final StaffAccess staffAccess;

    public StaffManager(StaffAccess staffAccess) {
        this.staffAccess = staffAccess;
    }

    public Staff createStaff(StaffRequest staffRequest) {
        if (staffRequest.getStaffType() != StaffType.CLINICIAN && staffRequest.getDepartment() == null) {
            throw new OrderExceptions.FulfillmentHasNoDepartmentOnCreationException();
        }

        Staff staff = new Staff(staffRequest.getName(), staffRequest.getStaffType(), staffRequest.getDepartment());
        staffAccess.saveStaff(staff);
        return staff;
    }

    public List<Staff> getStaffByDepartment(Department department) {
        return this.staffAccess.getStaffByDepartment(department);
    }

    public List<Staff> getClinicians() {
        return this.staffAccess.getClinicians();
    }

    public List<Staff> getAllStaff() {
        return staffAccess.getAllStaff();
    }
}
