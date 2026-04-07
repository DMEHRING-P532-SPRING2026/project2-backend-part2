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

  public static class OrderStaffNotSameAsRequesterException extends RuntimeException {
    public OrderStaffNotSameAsRequesterException(Long id) {
      super("Staff members on order does not match requester editing order for id: " + id);
    }
  }

  public static class UnknownStaffException extends RuntimeException {
    public UnknownStaffException(Long id) {
      super("Staff member with given id is not known: " + id);
    }
  }

  public static class NonClinicianCreateOrderException extends RuntimeException {
    public NonClinicianCreateOrderException(Long id) {
      super("Staff member is not a clinician trying to create order: " + id);
    }
  }

  public static class NonOwnerClinicianCancelOrderException extends RuntimeException {
    public NonOwnerClinicianCancelOrderException(Long id) {
      super("Staff member did not create order and is trying to cancel it: " + id);
    }
  }

}