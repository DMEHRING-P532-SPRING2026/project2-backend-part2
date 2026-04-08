package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.model.command.CommandType;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.utilities.NotificationPreferences;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderCommand implements Command {
    protected Order order;
    protected CommandType commandType;
    protected Long id;
    protected String event;
    protected Staff staff;
    protected Long staffId;
    protected NotificationPreferences preferences;


    private Status previousStatus;
    private List<Staff> previousStaff;
    private Priority previousPriority;
    private LocalDateTime previousDeadline;
    private String previousPatient;
    private String previousDescription;
    private Department previousDepartment;

    public void savePreviousState() {
        if (this.order == null) return;
        this.previousStatus = this.order.getStatus();
        this.previousStaff = new ArrayList<>(this.order.getStaff());
        this.previousPriority = this.order.getPriority();
        this.previousDeadline = this.order.getDeadline();
        this.previousPatient = this.order.getPatient();
        this.previousDescription = this.order.getDescription();
        this.previousDepartment = this.order.getDepartment();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CommandType getType() {
        return commandType;
    }

    public void setType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public NotificationPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(NotificationPreferences preferences) {
        this.preferences = preferences;
    }

    public Status getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(Status previousStatus) {
        this.previousStatus = previousStatus;
    }

    public List<Staff> getPreviousStaff() {
        return previousStaff;
    }

    public void setPreviousStaff(List<Staff> previousStaff) {
        this.previousStaff = previousStaff;
    }

    public Priority getPreviousPriority() {
        return previousPriority;
    }

    public void setPreviousPriority(Priority previousPriority) {
        this.previousPriority = previousPriority;
    }

    public LocalDateTime getPreviousDeadline() {
        return previousDeadline;
    }

    public void setPreviousDeadline(LocalDateTime previousDeadline) {
        this.previousDeadline = previousDeadline;
    }

    public String getPreviousPatient() {
        return previousPatient;
    }

    public void setPreviousPatient(String previousPatient) {
        this.previousPatient = previousPatient;
    }

    public String getPreviousDescription() {
        return previousDescription;
    }

    public void setPreviousDescription(String previousDescription) {
        this.previousDescription = previousDescription;
    }

    public Department getPreviousDepartment() {
        return previousDepartment;
    }

    public void setPreviousDepartment(Department previousDepartment) {
        this.previousDepartment = previousDepartment;
    }

    @Override
    public void execute() {

    }
}