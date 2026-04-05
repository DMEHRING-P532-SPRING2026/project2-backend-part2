package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.controller.BadgeController;
import iu.devinmehringer.project2.managers.order.OrderCommand;
import iu.devinmehringer.project2.model.order.Order;
import org.springframework.stereotype.Component;

@Component
public class InAppNotifier implements NotificationService {

    private final BadgeController badgeController;

    public InAppNotifier(BadgeController badgeController) {
        this.badgeController = badgeController;
    }

    @Override
    public void notify(Order order, String event, String actor) {
        badgeController.pushBadgeUpdate(order, event, actor);
    }

    @Override
    public void update(OrderCommand command, String event) {
        this.notify(command.getOrder(), event, command.getActor());
    }
}