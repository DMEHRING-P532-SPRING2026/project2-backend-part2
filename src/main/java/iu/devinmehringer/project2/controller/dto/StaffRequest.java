package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.staff.StaffType;

public class StaffRequest {
    private String name;
    private StaffType staffType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StaffType getStaffType() {
        return staffType;
    }

    public void setStaffType(StaffType staffType) {
        this.staffType = staffType;
    }
}
