package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.model.order.Order;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService extends Observer {
    void sendNotification(Order order, String event);
}
