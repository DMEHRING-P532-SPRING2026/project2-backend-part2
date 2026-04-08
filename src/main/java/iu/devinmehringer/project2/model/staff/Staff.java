package iu.devinmehringer.project2.model.staff;

import iu.devinmehringer.project2.model.order.Department;
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

    @Enumerated(EnumType.STRING)
    private Department department;

    @ManyToMany(mappedBy = "staff")
    private List<Order> orders;

    protected Staff() {}

    public Staff(String name, StaffType type, Department department) {
        this.name = name;
        this.type = type;
        if (department != null) {
            this.department = department;
        }
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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