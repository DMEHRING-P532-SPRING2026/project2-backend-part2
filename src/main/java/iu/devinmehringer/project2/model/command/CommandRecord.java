package iu.devinmehringer.project2.model.command;

import iu.devinmehringer.project2.model.staff.Staff;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="commands")
public class CommandRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime executedAt;
    @Enumerated(EnumType.STRING)
    private CommandType commandType;
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    private String other;

    protected CommandRecord() {}

    public CommandRecord(CommandType commandType, Long orderId, Staff staff, String other) {
        this.commandType = commandType;
        this.orderId = orderId;
        this.staff = staff;
        this.executedAt = LocalDateTime.now();
        this.other = other;
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

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
