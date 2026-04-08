package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.utilities.NotificationPreferences;

public class OrderRequest {
    private Department department;
    private String patient;
    private String description;
    private Priority priority;
    private Long staffId;
    private NotificationPreferences preferences;

    public Department getType() {
        return department;
    }

    public void setType(Department department) {
        this.department = department;
    }

    public String getPatient() { return patient; }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public NotificationPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(NotificationPreferences preferences) {
        this.preferences = preferences;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
