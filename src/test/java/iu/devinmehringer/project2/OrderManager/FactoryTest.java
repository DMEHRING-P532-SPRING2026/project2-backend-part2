package iu.devinmehringer.project2.OrderManager;

import iu.devinmehringer.project2.managers.order.OrderFactory;
import iu.devinmehringer.project2.model.order.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderFactoryTest {

    private OrderFactory orderFactory;

    @BeforeEach
    void setUp() {
        orderFactory = new OrderFactory();
    }

    @Test
    void createLabOrderShouldReturnLabOrder() {
        // Arrange/Act
        Order order = orderFactory.create(Type.LAB, "John Doe", "Alice Smith", "Blood panel", Priority.STAT);

        // Assert
        assertInstanceOf(LabOrder.class, order);
    }

    @Test
    void createLabOrderShouldHaveCorrectFields() {
        // Arrange/Act
        Order order = orderFactory.create(Type.LAB, "John Doe", "Alice Smith", "Blood panel", Priority.STAT);

        // Assert
        assertEquals(Type.LAB, order.getType());
        assertEquals("John Doe", order.getPatient());
        assertEquals("Alice Smith", order.getClinician());
        assertEquals("Blood panel", order.getDescription());
        assertEquals(Priority.STAT, order.getPriority());
    }

    @Test
    void createLabOrderShouldHavePendingStatus() {
        // Arrange/Act
        Order order = orderFactory.create(Type.LAB, "John Doe", "Alice Smith", "Blood panel", Priority.STAT);

        // Assert
        assertEquals(Status.PENDING, order.getStatus());
    }

    @Test
    void createMedicationOrderShouldReturnMedicationOrder() {
        // Arrange/Act
        Order order = orderFactory.create(Type.MEDICATION, "John Doe", "Alice Smith", "Aspirin", Priority.ROUTINE);

        // Assert
        assertInstanceOf(MedicationOrder.class, order);
    }

    @Test
    void createMedicationOrderShouldHaveCorrectFields() {
        // Arrange/Act
        Order order = orderFactory.create(Type.MEDICATION, "John Doe", "Alice Smith", "Aspirin", Priority.ROUTINE);

        // Assert
        assertEquals(Type.MEDICATION, order.getType());
        assertEquals("John Doe", order.getPatient());
        assertEquals("Alice Smith", order.getClinician());
        assertEquals("Aspirin", order.getDescription());
        assertEquals(Priority.ROUTINE, order.getPriority());
    }

    @Test
    void createMedicationOrderShouldHavePendingStatus() {
        // Arrange/Act
        Order order = orderFactory.create(Type.MEDICATION, "John Doe", "Alice Smith", "Aspirin", Priority.ROUTINE);

        // Assert
        assertEquals(Status.PENDING, order.getStatus());
    }

    @Test
    void createImagingOrderShouldReturnImagingOrder() {
        // Arrange/Act
        Order order = orderFactory.create(Type.IMAGING, "John Doe", "Alice Smith", "Chest X-Ray", Priority.URGENT);

        // Assert
        assertInstanceOf(ImagingOrder.class, order);
    }

    @Test
    void createImagingOrderShouldHaveCorrectFields() {
        // Arrange/Act
        Order order = orderFactory.create(Type.IMAGING, "John Doe", "Alice Smith", "Chest X-Ray", Priority.URGENT);

        // Assert
        assertEquals(Type.IMAGING, order.getType());
        assertEquals("John Doe", order.getPatient());
        assertEquals("Alice Smith", order.getClinician());
        assertEquals("Chest X-Ray", order.getDescription());
        assertEquals(Priority.URGENT, order.getPriority());
    }

    @Test
    void createImagingOrderShouldHavePendingStatus() {
        // Arrange/Act
        Order order = orderFactory.create(Type.IMAGING, "John Doe", "Alice Smith", "Chest X-Ray", Priority.URGENT);

        // Assert
        assertEquals(Status.PENDING, order.getStatus());
    }
}