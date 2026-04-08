package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.controller.BadgeController;
import iu.devinmehringer.project2.managers.order.OrderCommand;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.staff.Staff;
import iu.devinmehringer.project2.model.staff.StaffType;
import org.springframework.stereotype.Component;

@Component
public class InAppNotifier implements NotificationService {

    private final BadgeController badgeController;

    public InAppNotifier(BadgeController badgeController) {
        this.badgeController = badgeController;
    }

    @Override
    public void sendNotification(Order order, String event) {
        order.getStaff().stream()
                .filter(s -> s.getType() == StaffType.FULFILLMENT)
                .findFirst().ifPresent(actor -> badgeController.pushBadgeUpdate(order, event, actor.getId()));
    }

    @Override
    public void update(OrderCommand command, String event) {
        this.sendNotification(command.getOrder(), event);
    }
}