package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.managers.order.OrderCommand;
public interface Observer {
    void update(OrderCommand command, String event);
}