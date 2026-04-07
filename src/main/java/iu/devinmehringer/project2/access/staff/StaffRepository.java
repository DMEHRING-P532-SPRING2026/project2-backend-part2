package iu.devinmehringer.project2.access.staff;

import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

}
