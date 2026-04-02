package iu.devinmehringer.project2.controller.dto;

import iu.devinmehringer.project2.model.command.Type;
import java.time.LocalDateTime;

public class CommandResponse {
    private LocalDateTime executedAt;
    private Type type;
    private Long orderId;
    private String actor;

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
