package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.command.CommandType;
import iu.devinmehringer.project2.model.staff.Staff;

import java.time.LocalDateTime;

public class CommandResponse {
    private LocalDateTime executedAt;
    private CommandType commandType;
    private Long orderId;
    private Staff staff;

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public CommandType getType() {
        return commandType;
    }

    public void setType(CommandType commandType) {
        this.commandType = commandType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
}
