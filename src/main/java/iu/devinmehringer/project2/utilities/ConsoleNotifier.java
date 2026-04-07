package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.managers.order.OrderCommand;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.stereotype.Component;

@Component
public class ConsoleNotifier implements NotificationService {
    @Override
    public void notify(Order order, String event) {
        for (Staff staff : order.getStaff()) {
            System.out.println("Staff: " + staff +
                    ", notified about order: " + order + " for event: " + event);
        }
    }

    @Override
    public void update(OrderCommand command, String event) {
        this.notify(command.getOrder(), event);
    }
}
