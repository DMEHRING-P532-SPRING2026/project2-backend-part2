package iu.devinmehringer.project2.OrderManager;

import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.managers.order.TriageStrategyType;
import iu.devinmehringer.project2.managers.order.TriagingEngine;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriagingEngineTest {

    @Mock private OrderAccess orderAccess;
    @Mock private StaffAccess staffAccess;

    private TriagingEngine triagingEngine;
    private Staff staffMember;
    private Staff otherStaff;
    private Order statOrder;
    private Order urgentOrder;
    private Order routineOrder;

    @BeforeEach
    void setUp() {
        triagingEngine = new TriagingEngine(orderAccess, staffAccess);

        staffMember = new Staff("Jane Doe", StaffType.FULFILLMENT, Department.LAB);
        staffMember.setId(1L);

        otherStaff = new Staff("John Smith", StaffType.FULFILLMENT, Department.LAB);
        otherStaff.setId(2L);

        Staff clinician = new Staff("Dr. Smith", StaffType.CLINICIAN, null);
        clinician.setId(3L);

        statOrder = new Order(Department.LAB, "Patient A", "STAT test",
                Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(10), clinician);
        statOrder.setId(1L);

        urgentOrder = new Order(Department.LAB, "Patient B", "Urgent test",
                Priority.URGENT, Status.PENDING, LocalDateTime.now().minusMinutes(5), clinician);
        urgentOrder.setId(2L);

        routineOrder = new Order(Department.LAB, "Patient C", "Routine test",
                Priority.ROUTINE, Status.PENDING, LocalDateTime.now(), clinician);
        routineOrder.setId(3L);
    }

    @Test
    void priorityFirstShouldReturnStatOrdersFirst() {
        // Arrange
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.STAT, Department.LAB))
                .thenReturn(List.of(statOrder));
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.URGENT, Department.LAB))
                .thenReturn(List.of(urgentOrder));
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.ROUTINE, Department.LAB))
                .thenReturn(List.of(routineOrder));

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.PRIORITY_FIRST, Department.LAB);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(statOrder, result.get(0));
    }

    @Test
    void priorityFirstShouldPlaceRoutineOrdersLast() {
        // Arrange
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.STAT, Department.LAB))
                .thenReturn(List.of(statOrder));
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.URGENT, Department.LAB))
                .thenReturn(List.of(urgentOrder));
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.ROUTINE, Department.LAB))
                .thenReturn(List.of(routineOrder));

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.PRIORITY_FIRST, Department.LAB);

        // Assert
        assertEquals(routineOrder, result.get(result.size() - 1));
    }

    @Test
    void priorityFirstShouldReturnAllOrders() {
        // Arrange
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.STAT, Department.LAB))
                .thenReturn(List.of(statOrder));
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.URGENT, Department.LAB))
                .thenReturn(List.of(urgentOrder));
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(Priority.ROUTINE, Department.LAB))
                .thenReturn(List.of(routineOrder));

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.PRIORITY_FIRST, Department.LAB);

        // Assert
        assertEquals(3, result.size());
    }

    @Test
    void priorityFirstShouldReturnEmptyWhenNoOrders() {
        // Arrange
        when(orderAccess.getPendingOrdersByPriorityAndDepartment(any(), eq(Department.LAB)))
                .thenReturn(List.of());

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.PRIORITY_FIRST, Department.LAB);

        // Assert
        assertTrue(result.isEmpty());
    }


    @Test
    void deadlineFirstShouldReturnOrders() {
        // Arrange
        when(orderAccess.getPendingOrdersByDepartment(Department.LAB))
                .thenReturn(List.of(statOrder, urgentOrder, routineOrder));

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.DEADLINE_FIRST, Department.LAB);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
    }

    @Test
    void deadlineFirstShouldReturnEmptyWhenNoOrders() {
        // Arrange
        when(orderAccess.getPendingOrdersByDepartment(Department.LAB)).thenReturn(List.of());

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.DEADLINE_FIRST, Department.LAB);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void loadBalancingShouldReturnOrdersWhenStaffNotOverloaded() {
        // Arrange
        when(orderAccess.getPendingOrdersByDepartment(Department.LAB))
                .thenReturn(List.of(statOrder, urgentOrder));
        when(orderAccess.getByStaffContainingAndDepartmentAndStatus(staffMember, Department.LAB, Status.IN_PROGRESS))
                .thenReturn(List.of());
        when(staffAccess.getStaffByDepartment(Department.LAB))
                .thenReturn(List.of(staffMember, otherStaff));
        when(orderAccess.getByStaffContainingAndDepartmentAndStatus(otherStaff, Department.LAB, Status.IN_PROGRESS))
                .thenReturn(List.of());

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.LOAD_BALANCING, Department.LAB);

        // Assert
        assertFalse(result.isEmpty());
    }

    @Test
    void loadBalancingShouldReturnEmptyWhenStaffHasMoreThanOthers() {
        // Arrange
        Order inProgressOrder = new Order(Department.LAB, "Patient X", "In progress",
                Priority.STAT, Status.IN_PROGRESS, LocalDateTime.now(),
                new Staff("Dr. Jones", StaffType.CLINICIAN, null));

        when(orderAccess.getPendingOrdersByDepartment(Department.LAB))
                .thenReturn(List.of(statOrder));
        when(orderAccess.getByStaffContainingAndDepartmentAndStatus(staffMember, Department.LAB, Status.IN_PROGRESS))
                .thenReturn(List.of(inProgressOrder)); // staffMember has 1
        when(staffAccess.getStaffByDepartment(Department.LAB))
                .thenReturn(List.of(staffMember, otherStaff));
        when(orderAccess.getByStaffContainingAndDepartmentAndStatus(otherStaff, Department.LAB, Status.IN_PROGRESS))
                .thenReturn(List.of()); // otherStaff has 0

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.LOAD_BALANCING, Department.LAB);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void loadBalancingShouldReturnOrdersWhenEqualLoad() {
        // Arrange
        Order inProgressOrder = new Order(Department.LAB, "Patient X", "In progress",
                Priority.STAT, Status.IN_PROGRESS, LocalDateTime.now(),
                new Staff("Dr. Jones", StaffType.CLINICIAN, null));

        when(orderAccess.getPendingOrdersByDepartment(Department.LAB))
                .thenReturn(List.of(statOrder));
        when(orderAccess.getByStaffContainingAndDepartmentAndStatus(staffMember, Department.LAB, Status.IN_PROGRESS))
                .thenReturn(List.of(inProgressOrder)); // staffMember has 1
        when(staffAccess.getStaffByDepartment(Department.LAB))
                .thenReturn(List.of(staffMember, otherStaff));
        when(orderAccess.getByStaffContainingAndDepartmentAndStatus(otherStaff, Department.LAB, Status.IN_PROGRESS))
                .thenReturn(List.of(inProgressOrder)); // otherStaff also has 1

        // Act
        List<Order> result = triagingEngine.getPending(staffMember, TriageStrategyType.LOAD_BALANCING, Department.LAB);

        // Assert
        assertFalse(result.isEmpty());
    }
}