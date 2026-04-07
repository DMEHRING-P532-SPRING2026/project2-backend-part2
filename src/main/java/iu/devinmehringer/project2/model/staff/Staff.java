package iu.devinmehringer.project2.model.staff;

import iu.devinmehringer.project2.model.order.Order;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private StaffType type;

    @ManyToMany(mappedBy = "staff")
    private List<Order> orders;

    private Staff() {}

    public Staff(String name, StaffType type) {
        this.name = name;
        this.type = type;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StaffType getType() {
        return type;
    }

    public void setType(StaffType type) {
        this.type = type;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", orders=" + orders +
                '}';
    }
}