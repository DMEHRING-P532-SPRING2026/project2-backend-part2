package iu.devinmehringer.project2.controller;

import iu.devinmehringer.project2.model.order.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderExceptions.OrderNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OrderExceptions.OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleBadRequest(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
    }

    @ExceptionHandler(OrderExceptions.OrderClaimException.class)
    public ResponseEntity<String> handleOrderClaimException(OrderExceptions.OrderClaimException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Order can't be claimed");
    }

    @ExceptionHandler(OrderExceptions.OrderCancelException.class)
    public ResponseEntity<String> handleOrderCancelException(OrderExceptions.OrderCancelException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Order can't be canceled");
    }

    @ExceptionHandler(OrderExceptions.OrderSubmitException.class)
    public ResponseEntity<String> handleOrderSubmitException(OrderExceptions.OrderSubmitException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Order can't be submitted");
    }

    @ExceptionHandler(OrderExceptions.OrderActorException.class)
    public ResponseEntity<String> handleOrderActorException(OrderExceptions.OrderActorException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Requester not same as order actor");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResource(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
    }
}
