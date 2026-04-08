package iu.devinmehringer.project2.OrderManager;

import iu.devinmehringer.project2.access.command.CommandAccess;
import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.access.staff.StaffAccess;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.managers.order.OrderFactory;
import iu.devinmehringer.project2.managers.order.OrderManager;
import iu.devinmehringer.project2.managers.order.TriagingEngine;
import iu.devinmehringer.project2.model.command.CommandRecord;
import iu.devinmehringer.project2.model.command.CommandType;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UndoReplayTest {

    @Mock private OrderFactory orderFactory;
    @Mock private OrderAccess orderAccess;
    @Mock private StaffAccess staffAccess;
    @Mock private CommandAccess commandAccess;
    @Mock private ConsoleNotifier notificationService;
    @Mock private TriagingEngine triagingEngine;

    private OrderManager orderManager;
    private Staff clinician;
    private Staff fulfillmentStaff;
    private Order mockOrder;
    private OrderRequest clinicianRequest;
    private OrderRequest fulfillmentRequest;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager(orderFactory, orderAccess, staffAccess, commandAccess,
                triagingEngine, List.of(notificationService));

        clinician = new Staff("Dr. Smith", StaffType.CLINICIAN, null);
        clinician.setId(1L);

        fulfillmentStaff = new Staff("Jane Doe", StaffType.FULFILLMENT, Department.LAB);
        fulfillmentStaff.setId(2L);

        mockOrder = new Order(Department.LAB, "John Doe", "Blood panel",
                Priority.STAT, Status.PENDING, LocalDateTime.now(), clinician);
        mockOrder.setId(1L);

        NotificationPreferences prefs = new NotificationPreferences();
        prefs.setConsole(false);
        prefs.setInApp(false);
        prefs.setEmail(false);

        clinicianRequest = new OrderRequest();
        clinicianRequest.setStaffId(1L);
        clinicianRequest.setType(Department.LAB);
        clinicianRequest.setPatient("John Doe");
        clinicianRequest.setDescription("Blood panel");
        clinicianRequest.setPriority(Priority.STAT);
        clinicianRequest.setPreferences(prefs);

        fulfillmentRequest = new OrderRequest();
        fulfillmentRequest.setStaffId(2L);
        fulfillmentRequest.setPreferences(prefs);
    }


    @Test
    void undoWithNoCommandShouldThrow() {
        // Arrange — no commands executed yet

        // Act/Assert
        assertThrows(RuntimeException.class, () -> orderManager.undoLastCommand());
    }

    @Test
    void undoAfterClaimShouldRevertStatusToPending() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        orderManager.claimOrder(1L, fulfillmentRequest);

        // Act
        Order result = orderManager.undoLastCommand();

        // Assert
        assertEquals(Status.PENDING, result.getStatus());
    }

    @Test
    void undoAfterClaimShouldRemoveFulfillmentStaffFromOrder() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        orderManager.claimOrder(1L, fulfillmentRequest);

        // Act
        Order result = orderManager.undoLastCommand();

        // Assert
        assertFalse(result.getStaff().contains(fulfillmentStaff));
    }

    @Test
    void undoAfterCancelShouldRevertStatusToPending() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        orderManager.cancelOrder(1L, clinicianRequest);

        // Act
        Order result = orderManager.undoLastCommand();

        // Assert
        assertEquals(Status.PENDING, result.getStatus());
    }

    @Test
    void undoAfterSubmitShouldRevertStatusToInProgress() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        mockOrder.addStaff(fulfillmentStaff);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        orderManager.submitOrder(1L, fulfillmentRequest);

        // Act
        Order result = orderManager.undoLastCommand();

        // Assert
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void undoAfterCreateShouldCancelOrder() {
        // Arrange
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);
        orderManager.createOrder(clinicianRequest);

        // Act
        Order result = orderManager.undoLastCommand();

        // Assert
        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    void undoShouldLogUndoCommand() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        orderManager.claimOrder(1L, fulfillmentRequest);
        reset(commandAccess);

        // Act
        orderManager.undoLastCommand();

        // Assert
        verify(commandAccess).saveCommand(argThat(r -> r.getType() == CommandType.UNDO));
    }

    @Test
    void undoTwiceShouldThrowOnSecondUndo() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        orderManager.claimOrder(1L, fulfillmentRequest);
        orderManager.undoLastCommand();

        // Act/Assert
        assertThrows(RuntimeException.class, () -> orderManager.undoLastCommand());
    }

    @Test
    void undoShouldSaveOrderAfterRestoring() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        orderManager.claimOrder(1L, fulfillmentRequest);
        reset(orderAccess);

        // Act
        orderManager.undoLastCommand();

        // Assert
        verify(orderAccess).saveOrder(mockOrder);
    }


    @Test
    void replayClaimShouldClaimOrderAgain() {
        // Arrange
        CommandRecord claimRecord = new CommandRecord(CommandType.CLAIM, 1L, fulfillmentStaff, null);
        claimRecord.setId(10L);
        when(commandAccess.getCommandById(10L)).thenReturn(claimRecord);
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.replayCommand(10L, fulfillmentRequest);

        // Assert
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void replayCancelShouldCancelOrderAgain() {
        // Arrange
        CommandRecord cancelRecord = new CommandRecord(CommandType.CANCEL, 1L, clinician, null);
        cancelRecord.setId(11L);
        when(commandAccess.getCommandById(11L)).thenReturn(cancelRecord);
        mockOrder.setStatus(Status.PENDING);
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.replayCommand(11L, clinicianRequest);

        // Assert
        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    void replaySubmitShouldSubmitOrderAgain() {
        // Arrange
        CommandRecord submitRecord = new CommandRecord(CommandType.SUBMIT, 1L, fulfillmentStaff, null);
        submitRecord.setId(12L);
        when(commandAccess.getCommandById(12L)).thenReturn(submitRecord);
        mockOrder.setStatus(Status.IN_PROGRESS);
        mockOrder.addStaff(fulfillmentStaff);
        when(staffAccess.getStaffFromID(2L)).thenReturn(fulfillmentStaff);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.replayCommand(12L, fulfillmentRequest);

        // Assert
        assertEquals(Status.COMPLETED, result.getStatus());
    }

    @Test
    void replayCreateShouldCreateNewOrder() {
        // Arrange
        CommandRecord createRecord = new CommandRecord(CommandType.CREATE, 1L, clinician, null);
        createRecord.setId(13L);
        when(commandAccess.getCommandById(13L)).thenReturn(createRecord);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);
        when(staffAccess.getStaffFromID(1L)).thenReturn(clinician);

        Order newOrder = new Order(Department.LAB, "John Doe", "Blood panel",
                Priority.STAT, Status.PENDING, LocalDateTime.now(), clinician);
        newOrder.setId(2L);
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(newOrder);

        // Act
        Order result = orderManager.replayCommand(13L, clinicianRequest);

        // Assert
        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
    }

    @Test
    void replayUndoShouldThrow() {
        // Arrange
        CommandRecord undoRecord = new CommandRecord(CommandType.UNDO, 1L, clinician, null);
        undoRecord.setId(14L);
        when(commandAccess.getCommandById(14L)).thenReturn(undoRecord);

        // Act/Assert
        assertThrows(RuntimeException.class,
                () -> orderManager.replayCommand(14L, clinicianRequest));
    }
}