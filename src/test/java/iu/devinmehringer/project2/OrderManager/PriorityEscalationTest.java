package iu.devinmehringer.project2.OrderManager;

import iu.devinmehringer.project2.access.command.CommandAccess;
import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.managers.order.OrderFactory;
import iu.devinmehringer.project2.managers.order.OrderManager;
import iu.devinmehringer.project2.managers.order.TriagingEngine;
import iu.devinmehringer.project2.model.order.Department;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import iu.devinmehringer.project2.utilities.ConsoleNotifier;
import iu.devinmehringer.project2.utilities.NotificationPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriorityEscalationTest {

    @Mock private OrderFactory orderFactory;
    @Mock private OrderAccess orderAccess;
    @Mock private StaffAccess staffAccess;
    @Mock private CommandAccess commandAccess;
    @Mock private ConsoleNotifier notificationService;
    @Mock private TriagingEngine triagingEngine;

    private OrderManager orderManager;
    private Staff clinician;
    private NotificationPreferences noPrefs;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager(orderFactory, orderAccess, staffAccess, commandAccess,
                triagingEngine, List.of(notificationService));

        clinician = new Staff("Dr. Smith", StaffType.CLINICIAN, null);
        clinician.setId(1L);

        noPrefs = new NotificationPreferences();
        noPrefs.setConsole(false);
        noPrefs.setInApp(false);
        noPrefs.setEmail(false);
    }

    @Test
    void urgentOrderShouldEscalateToStatWhenRecentStatExists() {
        // Arrange
        Order urgentOrder = new Order(Department.LAB, "Patient B", "Urgent test",
                Priority.URGENT, Status.PENDING, LocalDateTime.now(), clinician);
        urgentOrder.setId(2L);

        Order recentStat = new Order(Department.LAB, "Patient A", "Stat test",
                Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(3), clinician);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(urgentOrder);
        when(orderAccess.getRecentStatOrders(eq(Department.LAB), any())).thenReturn(List.of(recentStat));

        OrderRequest request = new OrderRequest();
        request.setStaffId(1L);
        request.setType(Department.LAB);
        request.setPatient("Patient B");
        request.setDescription("Urgent test");
        request.setPriority(Priority.URGENT);
        request.setPreferences(noPrefs);

        // Act
        Order result = orderManager.createOrder(request);

        // Assert
        assertEquals(Priority.STAT, result.getPriority());
    }

    @Test
    void urgentOrderShouldNotEscalateWhenNoRecentStat() {
        // Arrange
        Order urgentOrder = new Order(Department.LAB, "Patient B", "Urgent test",
                Priority.URGENT, Status.PENDING, LocalDateTime.now(), clinician);
        urgentOrder.setId(2L);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(urgentOrder);
        when(orderAccess.getRecentStatOrders(eq(Department.LAB), any())).thenReturn(List.of());

        OrderRequest request = new OrderRequest();
        request.setStaffId(1L);
        request.setType(Department.LAB);
        request.setPatient("Patient B");
        request.setDescription("Urgent test");
        request.setPriority(Priority.URGENT);
        request.setPreferences(noPrefs);

        // Act
        Order result = orderManager.createOrder(request);

        // Assert
        assertEquals(Priority.URGENT, result.getPriority());
    }

    @Test
    void routineOrderShouldNeverEscalate() {
        // Arrange
        Order routineOrder = new Order(Department.LAB, "Patient C", "Routine test",
                Priority.ROUTINE, Status.PENDING, LocalDateTime.now(), clinician);
        routineOrder.setId(3L);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(routineOrder);

        OrderRequest request = new OrderRequest();
        request.setStaffId(1L);
        request.setType(Department.LAB);
        request.setPatient("Patient C");
        request.setDescription("Routine test");
        request.setPriority(Priority.ROUTINE);
        request.setPreferences(noPrefs);

        // Act
        Order result = orderManager.createOrder(request);

        // Assert
        assertEquals(Priority.ROUTINE, result.getPriority());
        verify(orderAccess, never()).getRecentStatOrders(any(), any());
    }

    @Test
    void escalatedOrderShouldGetStatDeadline() {
        // Arrange
        Order urgentOrder = new Order(Department.LAB, "Patient B", "Urgent test",
                Priority.URGENT, Status.PENDING, LocalDateTime.now(), clinician);
        urgentOrder.setId(2L);

        Order recentStat = new Order(Department.LAB, "Patient A", "Stat test",
                Priority.STAT, Status.PENDING, LocalDateTime.now().minusMinutes(2), clinician);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(urgentOrder);
        when(orderAccess.getRecentStatOrders(eq(Department.LAB), any())).thenReturn(List.of(recentStat));

        OrderRequest request = new OrderRequest();
        request.setStaffId(1L);
        request.setType(Department.LAB);
        request.setPatient("Patient B");
        request.setDescription("Urgent test");
        request.setPriority(Priority.URGENT);
        request.setPreferences(noPrefs);

        // Act
        Order result = orderManager.createOrder(request);

        // Assert
        assertNotNull(result.getDeadline());
        assertTrue(result.getDeadline().isAfter(LocalDateTime.now().plusMinutes(25)));
        assertTrue(result.getDeadline().isBefore(LocalDateTime.now().plusMinutes(35)));
    }
}