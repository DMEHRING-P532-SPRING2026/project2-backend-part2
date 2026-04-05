package iu.devinmehringer.project2.managers.order;

import iu.devinmehringer.project2.model.command.Type;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.utilities.NotificationPreferences;

public class OrderCommand implements Command {
    protected Order order;
    protected Type type;
    protected Long id;
    protected String event;
    protected String actor;
    protected NotificationPreferences preferences;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public NotificationPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(NotificationPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void execute() {

    }
}
