package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.OrderType;
import iu.devinmehringer.project2.utilities.NotificationPreferences;

public class OrderRequest {
    private OrderType orderType;
    private String patient;
    private String description;
    private Priority priority;
    private Long staffId;
    private NotificationPreferences preferences;

    public OrderType getType() {
        return orderType;
    }

    public void setType(OrderType orderType) {
        this.orderType = orderType;
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

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
