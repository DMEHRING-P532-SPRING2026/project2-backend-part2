package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.model.command.CommandType;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.utilities.NotificationPreferences;

public class OrderCommand implements Command {
    protected Order order;
    protected CommandType commandType;
    protected Long id;
    protected String event;
    protected Staff staff;
    protected Long staffId;
    protected NotificationPreferences preferences;

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

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Override
    public void execute() {

    }
}
