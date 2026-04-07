package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.managers.order.OrderCommand;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.staff.Staff;
import org.springframework.stereotype.Component;

@Component
public class EmailNotifier implements NotificationService {
    @Override
    public void notify(Order order, String event) {
        for (Staff staff : order.getStaff()) {
            System.out.println(staff.getName().split(" ")[0] +
                    "@example.com: Your order: " + order + " has been processed for event: " + event);
        }
    }

    @Override
    public void update(OrderCommand command, String event) {
        this.notify(command.getOrder(), event);
    }
}
