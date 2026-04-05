package iu.devinmehringer.project2.utilities;

import iu.devinmehringer.project2.managers.order.OrderCommand;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(OrderCommand command, String event, NotificationPreferences preferences);
}