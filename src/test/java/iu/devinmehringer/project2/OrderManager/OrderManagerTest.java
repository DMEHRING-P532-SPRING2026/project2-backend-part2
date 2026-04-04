package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.access.command.CommandAccess;
import iu.devinmehringer.project2.access.order.OrderAccess;
import iu.devinmehringer.project2.controller.OrderExceptions;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.model.command.CommandRecord;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Priority;
import iu.devinmehringer.project2.model.order.Status;
import iu.devinmehringer.project2.utilities.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderManagerTest {

    @Mock private OrderFactory orderFactory;
    @Mock private OrderAccess orderAccess;
    @Mock private CommandAccess commandAccess;
    @Mock private NotificationService notificationService;
    @Mock private TriagingEngine triagingEngine;

    private OrderManager orderManager;
    private OrderRequest orderRequest;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        TriageStrategy mockStrategy = mock(TriageStrategy.class);
        lenient().when(mockStrategy.getSortedOrders()).thenReturn(List.of());
        lenient().when(mockStrategy.getSortedOrders(any())).thenReturn(List.of());
        lenient().when(triagingEngine.priorityFirst()).thenReturn(mockStrategy);
        orderManager = new OrderManager(orderFactory, orderAccess, notificationService, commandAccess, triagingEngine);

        orderRequest = new OrderRequest();
        orderRequest.setType(iu.devinmehringer.project2.model.order.Type.LAB);
        orderRequest.setPatient("John Doe");
        orderRequest.setClinician("Alice Smith");
        orderRequest.setDescription("Blood panel");
        orderRequest.setPriority(Priority.STAT);
        orderRequest.setActor("Alice Smith");

        mockOrder = new Order(
                iu.devinmehringer.project2.model.order.Type.LAB,
                "John Doe",
                "Alice Smith",
                "Blood panel",
                Priority.STAT,
                Status.PENDING,
                java.time.LocalDateTime.now()
        );
        mockOrder.setId(1L);
        mockOrder.setCurrentActor("Alice Smith");
    }

    @Test
    void createOrderShouldReturnOrder() {
        // Arrange
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        Order result = orderManager.createOrder(orderRequest);

        // Assert
        assertNotNull(result);
        verify(orderAccess).saveOrder(mockOrder);
    }

    @Test
    void createOrderShouldLogCommand() {
        // Arrange
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(commandAccess).saveCommand(any(CommandRecord.class));
    }

    @Test
    void createOrderShouldNotifyObservers() {
        // Arrange
        when(orderFactory.create(any(), any(), any(), any(), any())).thenReturn(mockOrder);

        // Act
        orderManager.createOrder(orderRequest);

        // Assert
        verify(notificationService).update(any(), eq("Create Order"));
    }

    @Test
    void claimOrderShouldSetStatusToInProgress() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.claimOrder(1L, orderRequest);

        // Assert
        assertEquals(Status.IN_PROGRESS, result.getStatus());
    }

    @Test
    void claimOrderShouldSetCurrentActor() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.claimOrder(1L, orderRequest);

        // Assert
        assertEquals("Alice Smith", result.getCurrentActor());
    }

    @Test
    void claimOrderShouldThrowWhenNotPending() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderClaimException.class,
                () -> orderManager.claimOrder(1L, orderRequest));
    }

    @Test
    void claimOrderShouldThrowWhenCancelled() {
        // Arrange
        mockOrder.setStatus(Status.CANCELLED);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderClaimException.class,
                () -> orderManager.claimOrder(1L, orderRequest));
    }


    @Test
    void cancelOrderShouldSetStatusToCancelled() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.cancelOrder(1L, orderRequest);

        // Assert
        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    void cancelOrderShouldThrowWhenNotPending() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderCancelException.class,
                () -> orderManager.cancelOrder(1L, orderRequest));
    }

    @Test
    void cancelOrderShouldThrowWhenAlreadyCancelled() {
        // Arrange
        mockOrder.setStatus(Status.CANCELLED);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderCancelException.class,
                () -> orderManager.cancelOrder(1L, orderRequest));
    }

    @Test
    void submitOrderShouldSetStatusToCompleted() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act
        Order result = orderManager.submitOrder(1L, orderRequest);

        // Assert
        assertEquals(Status.COMPLETED, result.getStatus());
    }

    @Test
    void submitOrderShouldThrowWhenNotInProgress() {
        // Arrange
        mockOrder.setStatus(Status.PENDING);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderSubmitException.class,
                () -> orderManager.submitOrder(1L, orderRequest));
    }

    @Test
    void submitOrderShouldThrowWhenWrongActor() {
        // Arrange
        mockOrder.setStatus(Status.IN_PROGRESS);
        mockOrder.setCurrentActor("Dr. Jones");
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderActorException.class,
                () -> orderManager.submitOrder(1L, orderRequest));
    }

    @Test
    void submitOrderShouldThrowWhenCompleted() {
        // Arrange
        mockOrder.setStatus(Status.COMPLETED);
        when(orderAccess.getOrderById(1L)).thenReturn(mockOrder);

        // Act/Assert
        assertThrows(OrderExceptions.OrderSubmitException.class,
                () -> orderManager.submitOrder(1L, orderRequest));
    }


    @Test
    void getPendingOrdersShouldReturnSortedOrders() {
        // Arrange
        TriageStrategy mockStrategy = mock(TriageStrategy.class);
        when(mockStrategy.getSortedOrders()).thenReturn(List.of(mockOrder));
        when(triagingEngine.priorityFirst()).thenReturn(mockStrategy);
        orderManager = new OrderManager(orderFactory, orderAccess, notificationService, commandAccess, triagingEngine);

        // Act
        List<Order> result = orderManager.getPendingOrders();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
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
}