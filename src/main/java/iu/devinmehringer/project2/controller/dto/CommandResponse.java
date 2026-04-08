package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.command.CommandType;

import java.time.LocalDateTime;

public class CommandResponse {
    private Long id;
    private LocalDateTime executedAt;
    private CommandType commandType;
    private Long orderId;
    private String staffName;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public LocalDateTime getExecutedAt() { return executedAt; }

    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public CommandType getType() { return commandType; }

    public void setType(CommandType commandType) { this.commandType = commandType; }

    public Long getOrderId() { return orderId; }

    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public CommandType getCommandType() { return commandType; }

    public void setCommandType(CommandType commandType) { this.commandType = commandType; }

    public String getStaffName() { return staffName; }

    public void setStaffName(String staffName) { this.staffName = staffName; }
}