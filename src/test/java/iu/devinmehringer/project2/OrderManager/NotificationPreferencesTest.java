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
import iu.devinmehringer.project2.utilities.EmailNotifier;
import iu.devinmehringer.project2.utilities.InAppNotifier;
import iu.devinmehringer.project2.utilities.NotificationPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPreferencesTest {

    @Mock private OrderFactory orderFactory;
    @Mock private OrderAccess orderAccess;
    @Mock private StaffAccess staffAccess;
    @Mock private CommandAccess commandAccess;
    @Mock private ConsoleNotifier consoleNotifier;
    @Mock private EmailNotifier emailNotifier;
    @Mock private InAppNotifier inAppNotifier;
    @Mock private TriagingEngine triagingEngine;

    private OrderManager orderManager;
    private Staff clinician;
    private Order mockOrder;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager(orderFactory, orderAccess, staffAccess, commandAccess,
                triagingEngine, List.of(consoleNotifier, emailNotifier, inAppNotifier));

        clinician = new Staff("Dr. Smith", StaffType.CLINICIAN, null);
        clinician.setId(1L);

        mockOrder = new Order(Department.LAB, "John Doe", "Blood panel",
                Priority.STAT, Status.PENDING, LocalDateTime.now(), clinician);
        mockOrder.setId(1L);

        orderRequest = new OrderRequest();
        orderRequest.setStaffId(1L);
        orderRequest.setType(Department.LAB);
        orderRequest.setPatient("John Doe");
        orderRequest.setDescription("Blood panel");
        orderRequest.setPriority(Priority.STAT);
    }

    @Test
    void onlyConsoleEnabledShouldOnlyNotifyConsole() {
        // Arrange
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setConsole(true);
        prefs.setInApp(false);
        prefs.setEmail(false);
        orderRequest.setPreferences(prefs);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(consoleNotifier).update(any(), any());
        verify(emailNotifier, never()).update(any(), any());
        verify(inAppNotifier, never()).update(any(), any());
    }

    @Test
    void onlyEmailEnabledShouldOnlyNotifyEmail() {
        // Arrange
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setConsole(false);
        prefs.setInApp(false);
        prefs.setEmail(true);
        orderRequest.setPreferences(prefs);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(emailNotifier).update(any(), any());
        verify(consoleNotifier, never()).update(any(), any());
        verify(inAppNotifier, never()).update(any(), any());
    }

    @Test
    void onlyInAppEnabledShouldOnlyNotifyInApp() {
        // Arrange
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setConsole(false);
        prefs.setInApp(true);
        prefs.setEmail(false);
        orderRequest.setPreferences(prefs);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(inAppNotifier).update(any(), any());
        verify(consoleNotifier, never()).update(any(), any());
        verify(emailNotifier, never()).update(any(), any());
    }

    @Test
    void allEnabledShouldNotifyAllChannels() {
        // Arrange
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setConsole(true);
        prefs.setInApp(true);
        prefs.setEmail(true);
        orderRequest.setPreferences(prefs);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(consoleNotifier).update(any(), any());
        verify(emailNotifier).update(any(), any());
        verify(inAppNotifier).update(any(), any());
    }

    @Test
    void noneEnabledShouldNotifyNoChannels() {
        // Arrange
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setConsole(false);
        prefs.setInApp(false);
        prefs.setEmail(false);
        orderRequest.setPreferences(prefs);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(consoleNotifier, never()).update(any(), any());
        verify(emailNotifier, never()).update(any(), any());
        verify(inAppNotifier, never()).update(any(), any());
    }

    @Test
    void nullPreferencesShouldNotifyNoChannels() {
        // Arrange
        orderRequest.setPreferences(null);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(consoleNotifier, never()).update(any(), any());
        verify(emailNotifier, never()).update(any(), any());
        verify(inAppNotifier, never()).update(any(), any());
    }

    @Test
    void consoleAndEmailEnabledShouldNotifyBothButNotInApp() {
        // Arrange
        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setConsole(true);
        prefs.setInApp(false);
        prefs.setEmail(true);
        orderRequest.setPreferences(prefs);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(consoleNotifier).update(any(), any());
        verify(emailNotifier).update(any(), any());
        verify(inAppNotifier, never()).update(any(), any());
    }
}