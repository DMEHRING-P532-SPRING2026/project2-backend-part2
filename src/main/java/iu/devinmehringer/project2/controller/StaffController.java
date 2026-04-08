package iu.devinmehringer.project2.controller;

import iu.devinmehringer.project2.controller.dto.StaffRequest;
import iu.devinmehringer.project2.controller.dto.StaffResponse;
import iu.devinmehringer.project2.managers.staff.StaffManager;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffManager staffManager;

    public StaffController(StaffManager staffManager) {
        this.staffManager = staffManager;
    }

    private static class StaffMapper{
        public static StaffResponse toDTO(Staff staff) {
            StaffResponse staffResponse = new StaffResponse();
            staffResponse.setDepartment(staff.getDepartment());
            staffResponse.setName(staff.getName());
            staffResponse.setType(staff.getType());
            staffResponse.setId(staff.getId());
            return staffResponse;
        }
    }

    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(@RequestBody StaffRequest staffRequest) {
        Staff staff = staffManager.createStaff(staffRequest);
        return ResponseEntity.ok(StaffMapper.toDTO(staff));
    }

    @GetMapping("/department")
    public ResponseEntity<List<StaffResponse>> getStaffByDepartment(@RequestParam Department department) {
        return ResponseEntity.ok(staffManager.getStaffByDepartment(department)
                .stream()
                .map(StaffMapper::toDTO)
                .toList());
    }

    @GetMapping("/clinicians")
    public ResponseEntity<List<StaffResponse>> getClinicians() {
        return ResponseEntity.ok(staffManager.getClinicians()
                .stream()
                .map(StaffMapper::toDTO)
                .toList());
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(staffManager.getAllStaff()
                .stream()
                .map(StaffMapper::toDTO)
                .toList());
    }


}
