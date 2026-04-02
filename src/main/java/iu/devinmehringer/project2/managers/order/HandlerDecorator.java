package iu.devinmehringer.project2.managers.order;

public class HandlerDecorator implements Handler {
    private final Handler handler;

    public HandlerDecorator(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(OrderCommand command) {
        handler.handle(command);
    }
}
