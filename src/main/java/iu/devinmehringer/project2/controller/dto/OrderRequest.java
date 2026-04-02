package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Type;

public class OrderRequest {
    private Type type;
    private String patient;
    private String clinician;
    private String description;
    private Priority priority;
    private String actor;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPatient() { return patient; }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getClinician() {
        return clinician;
    }

    public void setClinician(String clinician) {
        this.clinician = clinician;
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

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
