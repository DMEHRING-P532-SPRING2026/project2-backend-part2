package iu.devinmehringer.project2.access.staff;

import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByType(StaffType type);
    List<Staff> findByTypeAndDepartment(StaffType type, Department department);
}
