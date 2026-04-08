package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.staff.StaffType;


public class StaffResponse {
    private long id;
    private String name;
    private StaffType type;
    private Department department;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StaffType getType() {
        return type;
    }

    public void setType(StaffType type) {
        this.type = type;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
