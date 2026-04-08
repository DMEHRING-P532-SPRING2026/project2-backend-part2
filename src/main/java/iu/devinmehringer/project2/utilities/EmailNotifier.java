package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.managers.order.OrderCommand;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier implements NotificationService {

    @Override
    public void sendNotification(Order order, String event) {
        for (Staff staff : order.getStaff()) {
            String email = staff.getName().trim().toLowerCase()
                    .replaceAll("^dr\\.\\s*", "")
                    .replace(" ", ".") + "@example.com";
            System.out.println(email + ": Order #" + order.getId() +
                    " for patient " + order.getPatient() +
                    " has been processed for event: " + event);
        }
    }

    @Override
    public void update(OrderCommand command, String event) {
        this.sendNotification(command.getOrder(), event);
    }
}