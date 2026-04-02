package iu.devinmehringer.project2.controller;

public class OrderExceptions {

  public static class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
      super("Order not found: " + id);
    }
  }

  public static class OrderClaimException extends RuntimeException {
    public OrderClaimException(Long id) {
      super("Order already executed: " + id);
    }
  }

  public static class OrderCancelException extends RuntimeException {
    public OrderCancelException(Long id) {
      super("Order can't be canceled: " + id);
    }
  }

  public static class OrderSubmitException extends RuntimeException {
    public OrderSubmitException(Long id) {
      super("Order failed to submit: " + id);
    }
  }

  public static class OrderActorException extends RuntimeException {
    public OrderActorException(Long id) {
      super("Actor on order is not same requester on order: " + id);
    }
  }

}