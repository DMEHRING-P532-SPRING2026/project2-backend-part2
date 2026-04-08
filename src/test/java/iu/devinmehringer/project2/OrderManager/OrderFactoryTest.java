package iu.devinmehringer.project2.OrderManager;

import iu.devinmehringer.project2.managers.order.OrderFactory;
import iu.devinmehringer.project2.model.order.*;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderFactoryTest {

    private OrderFactory orderFactory;
    private Staff clinician;

    @BeforeEach
    void setUp() {
        orderFactory = new OrderFactory();
        clinician = new Staff("Dr. Smith", StaffType.CLINICIAN, null);
        clinician.setId(1L);
    }

    @Test
    void createLabOrderShouldReturnLabOrder() {
        // Arrange/Act
        Order order = orderFactory.create(Department.LAB, "John Doe", clinician, "Blood panel", Priority.STAT);

        // Assert
        assertInstanceOf(LabOrder.class, order);
    }

    @Test
    void createLabOrderShouldHaveCorrectFields() {
        // Arrange/Act
        Order order = orderFactory.create(Department.LAB, "John Doe", clinician, "Blood panel", Priority.STAT);

        // Assert
        assertEquals(Department.LAB, order.getType());
        assertEquals("John Doe", order.getPatient());
        assertEquals("Blood panel", order.getDescription());
        assertEquals(Priority.STAT, order.getPriority());
    }

    @Test
    void createLabOrderShouldHavePendingStatus() {
        // Arrange/Act
        Order order = orderFactory.create(Department.LAB, "John Doe", clinician, "Blood panel", Priority.STAT);

        // Assert
        assertEquals(Status.PENDING, order.getStatus());
    }

    @Test
    void createLabOrderShouldHaveClinicianInStaff() {
        // Arrange/Act
        Order order = orderFactory.create(Department.LAB, "John Doe", clinician, "Blood panel", Priority.STAT);

        // Assert
        assertTrue(order.getStaff().contains(clinician));
    }

    @Test
    void createMedicationOrderShouldReturnMedicationOrder() {
        // Arrange/Act
        Order order = orderFactory.create(Department.MEDICATION, "John Doe", clinician, "Aspirin", Priority.ROUTINE);

        // Assert
        assertInstanceOf(MedicationOrder.class, order);
    }

    @Test
    void createMedicationOrderShouldHaveCorrectFields() {
        // Arrange/Act
        Order order = orderFactory.create(Department.MEDICATION, "John Doe", clinician, "Aspirin", Priority.ROUTINE);

        // Assert
        assertEquals(Department.MEDICATION, order.getType());
        assertEquals("John Doe", order.getPatient());
        assertEquals("Aspirin", order.getDescription());
        assertEquals(Priority.ROUTINE, order.getPriority());
    }

    @Test
    void createMedicationOrderShouldHavePendingStatus() {
        // Arrange/Act
        Order order = orderFactory.create(Department.MEDICATION, "John Doe", clinician, "Aspirin", Priority.ROUTINE);

        // Assert
        assertEquals(Status.PENDING, order.getStatus());
    }

    @Test
    void createImagingOrderShouldReturnImagingOrder() {
        // Arrange/Act
        Order order = orderFactory.create(Department.IMAGING, "John Doe", clinician, "Chest X-Ray", Priority.URGENT);

        // Assert
        assertInstanceOf(ImagingOrder.class, order);
    }

    @Test
    void createImagingOrderShouldHaveCorrectFields() {
        // Arrange/Act
        Order order = orderFactory.create(Department.IMAGING, "John Doe", clinician, "Chest X-Ray", Priority.URGENT);

        // Assert
        assertEquals(Department.IMAGING, order.getType());
        assertEquals("John Doe", order.getPatient());
        assertEquals("Chest X-Ray", order.getDescription());
        assertEquals(Priority.URGENT, order.getPriority());
    }

    @Test
    void createImagingOrderShouldHavePendingStatus() {
        // Arrange/Act
        Order order = orderFactory.create(Department.IMAGING, "John Doe", clinician, "Chest X-Ray", Priority.URGENT);

        // Assert
        assertEquals(Status.PENDING, order.getStatus());
    }

    @Test
    void createAllThreeTypesShouldProduceDifferentInstances() {
        // Arrange/Act
        Order lab = orderFactory.create(Department.LAB, "Patient A", clinician, "CBC", Priority.STAT);
        Order med = orderFactory.create(Department.MEDICATION, "Patient B", clinician, "Aspirin", Priority.ROUTINE);
        Order img = orderFactory.create(Department.IMAGING, "Patient C", clinician, "X-Ray", Priority.URGENT);

        // Assert
        assertNotSame(lab, med);
        assertNotSame(med, img);
        assertNotSame(lab, img);
    }
}