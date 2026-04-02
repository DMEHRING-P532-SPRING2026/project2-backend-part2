package iu.devinmehringer.project2.model.command;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="commands")
public class CommandRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime executedAt;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(name = "order_id")
    private Long orderId;
    private String actor;

    protected CommandRecord() {}

    public CommandRecord(Type type, Long orderId, String actor) {
        this.type = type;
        this.orderId = orderId;
        this.actor = actor;
        this.executedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
