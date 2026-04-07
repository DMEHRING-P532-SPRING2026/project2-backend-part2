package iu.devinmehringer.project2.managers.staff;

import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.controller.dto.StaffRequest;
import iu.devinmehringer.project2.model.staff.Staff;

public class StaffManager {
    private final StaffAccess staffAccess;

    public StaffManager(StaffAccess staffAccess) {
        this.staffAccess = staffAccess;
    }

    public Staff createStaff(StaffRequest staffRequest) {
        Staff staff = new Staff(staffRequest.getName(), staffRequest.getStaffType());
        staffAccess.saveStaff(staff);
        return staff;
    }

    // TODO staff stuff haha I love restructuring code
}
