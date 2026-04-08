package iu.devinmehringer.project2.OrderManager;

import iu.devinmehringer.project2.access.command.CommandAccess;
import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.managers.order.OrderFactory;
import iu.devinmehringer.project2.managers.order.OrderManager;
import iu.devinmehringer.project2.managers.order.TriagingEngine;
import iu.devinmehringer.project2.model.command.CommandRecord;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderManagerTest {

    @Mock private OrderFactory orderFactory;
    @Mock private OrderAccess orderAccess;
    @Mock private StaffAccess staffAccess;
    @Mock private CommandAccess commandAccess;
    @Mock private ConsoleNotifier notificationService;
    @Mock private TriagingEngine triagingEngine;

    private OrderManager orderManager;
    private OrderRequest clinicianRequest;
    private OrderRequest fulfillmentRequest;
    private Order mockOrder;
    private Staff clinician;
    private Staff fulfillmentStaff;
    private NotificationPreferences allEnabled;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager(orderFactory, orderAccess, staffAccess, commandAccess,
                triagingEngine, List.of(notificationService));

        clinician = new Staff("Dr. Smith", StaffType.CLINICIAN, null);
        clinician.setId(1L);

        fulfillmentStaff = new Staff("Jane Doe", StaffType.FULFILLMENT, Department.LAB);
        fulfillmentStaff.setId(2L);

        allEnabled = new NotificationPreferences();
        allEnabled.setConsole(true);
        allEnabled.setInApp(true);
        allEnabled.setEmail(true);

        clinicianRequest = new OrderRequest();
        clinicianRequest.setStaffId(1L);
        clinicianRequest.setType(Department.LAB);
        clinicianRequest.setPatient("John Doe");
        clinicianRequest.setDescription("Blood panel");
        clinicianRequest.setPriority(Priority.STAT);
        clinicianRequest.setPreferences(allEnabled);

        fulfillmentRequest = new OrderRequest();
        fulfillmentRequest.setStaffId(2L);
        fulfillmentRequest.setPreferences(allEnabled);

        mockOrder = new Order(Department.LAB, "John Doe", "Blood panel",
                Priority.STAT, Status.PENDING, LocalDateTime.now(), clinician);
        mockOrder.setId(1L);
    }

    @Test
    void createOrderShouldReturnOrder() {
        // Arrange
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        Order result = orderManager.createOrder(clinicianRequest);

        // Assert
        assertNotNull(result);
    }

    @Test
    void createOrderShouldSaveOrder() {
        // Arrange
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(clinicianRequest);

        // Assert
        verify(orderAccess, atLeastOnce()).saveOrder(mockOrder);
    }

    @Test
    void createOrderShouldLogCommand() {
        // Arrange
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(clinicianRequest);

        // Assert
        verify(commandAccess).saveCommand(any(CommandRecord.class));
    }

    @Test
    void createOrderShouldNotifyObservers() {
        // Arrange
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(clinicianRequest);

        // Assert
        verify(notificationService).update(any(), eq("Create Order"));
    }

    @Test
    void createOrderShouldThrowWhenNonClinicianCreates() {
        // Arrange
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        clinicianRequest.setStaffId(2L);

        // Act/Assert
        assertThrows(OrderExceptions.NonClinicianCreateOrderException.class,
                () -> orderManager.createOrder(clinicianRequest));
    }

    @Test
    void createOrderShouldThrowWhenUnknownStaff() {
        // Arrange
        when(staffAccess.getStaffFromID(99L)).thenReturn(null);
        clinicianRequest.setStaffId(99L);

        // Act/Assert
        assertThrows(OrderExceptions.UnknownStaffException.class,
                () -> orderManager.createOrder(clinicianRequest));
    }

    @Test
    void createOrderShouldNotNotifyWhenPreferencesAllFalse() {
        // Arrange
        NotificationPreferences none = new NotificationPreferences();
        none.setConsole(false);
        none.setInApp(false);
        none.setEmail(false);
        clinicianRequest.setPreferences(none);

        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(clinicianRequest);

        // Assert
        verify(notificationService, never()).update(any(), any());
    }

    @Test
    void createOrderShouldSetStatDeadline() {
        // Arrange
        clinicianRequest.setPriority(Priority.STAT);
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        Order result = orderManager.createOrder(clinicianRequest);

        // Assert
        assertNotNull(result.getDeadline());
        assertTrue(result.getDeadline().isAfter(LocalDateTime.now()));
    }


    @Test
    void claimOrderShouldSetStatusToInProgress() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.claimOrder(1L, fulfillmentRequest);

        // Assert
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void claimOrderShouldAddFulfillmentStaffToOrder() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.claimOrder(1L, fulfillmentRequest);

        // Assert
        assertTrue(result.getStaff().contains(fulfillmentStaff));
    }

    @Test
    void claimOrderShouldThrowWhenAlreadyInProgress() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderClaimException.class,
                () -> orderManager.claimOrder(1L, fulfillmentRequest));
    }

    @Test
    void claimOrderShouldThrowWhenCancelled() {
        // Arrange
        mockOrder.setStatus(Status.CANCELLED);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderClaimException.class,
                () -> orderManager.claimOrder(1L, fulfillmentRequest));
    }

    @Test
    void claimOrderShouldThrowWhenCompleted() {
        // Arrange
        mockOrder.setStatus(Status.COMPLETED);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderClaimException.class,
                () -> orderManager.claimOrder(1L, fulfillmentRequest));
    }

    @Test
    void claimOrderShouldLogCommand() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        orderManager.claimOrder(1L, fulfillmentRequest);

        // Assert
        verify(commandAccess).saveCommand(any(CommandRecord.class));
    }

    @Test
    void cancelOrderShouldSetStatusToCancelled() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.cancelOrder(1L, clinicianRequest);

        // Assert
        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    void cancelOrderShouldThrowWhenNonClinician() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.NonOwnerClinicianCancelOrderException.class,
                () -> orderManager.cancelOrder(1L, fulfillmentRequest));
    }

    @Test
    void cancelOrderShouldLogCommand() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        orderManager.cancelOrder(1L, clinicianRequest);

        // Assert
        verify(commandAccess).saveCommand(any(CommandRecord.class));
    }

    @Test
    void cancelOrderShouldAllowCancelInProgress() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.cancelOrder(1L, clinicianRequest);

        // Assert
        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    void submitOrderShouldSetStatusToCompleted() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        mockOrder.addStaff(fulfillmentStaff);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.submitOrder(1L, fulfillmentRequest);

        // Assert
        assertEquals(Status.COMPLETED, result.getStatus());
    }

    @Test
    void submitOrderShouldThrowWhenNotInProgress() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        mockOrder.addStaff(fulfillmentStaff); // ← add staff so requester check passes
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderSubmitException.class,
                () -> orderManager.submitOrder(1L, fulfillmentRequest));
    }

    @Test
    void submitOrderShouldThrowWhenStaffNotOnOrder() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        // fulfillmentStaff NOT added to order staff
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderStaffNotSameAsRequesterException.class,
                () -> orderManager.submitOrder(1L, fulfillmentRequest));
    }

    @Test
    void submitOrderShouldThrowWhenAlreadyCompleted() {
        // Arrange
        mockOrder.setStatus(Status.COMPLETED);
        mockOrder.addStaff(fulfillmentStaff); // ← add staff so requester check passes
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderSubmitException.class,
                () -> orderManager.submitOrder(1L, fulfillmentRequest));
    }

    @Test
    void submitOrderShouldLogCommand() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        mockOrder.addStaff(fulfillmentStaff);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        orderManager.submitOrder(1L, fulfillmentRequest);

        // Assert
        verify(commandAccess).saveCommand(any(CommandRecord.class));
    }

    @Test
    void getOrderByIdShouldReturnOrder() {
        // Arrange
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.getOrderById(1L);

        // Assert
        assertEquals(mockOrder, result);
    }

    @Test
    void getPendingOrdersShouldDelegateToTriagingEngine() {
        // Arrange
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(triagingEngine.getPending(any(), any(), any())).thenReturn(List.of(mockOrder));

        // Act
        List<Order> result = orderManager.getPendingOrders(2L,
                iu.devinmehringer.project2.managers.order.TriageStrategyType.PRIORITY_FIRST, Department.LAB);

        // Assert
        assertFalse(result.isEmpty());
        verify(triagingEngine).getPending(eq(fulfillmentStaff),
                eq(iu.devinmehringer.project2.managers.order.TriageStrategyType.PRIORITY_FIRST),
                eq(Department.LAB));
    }
}