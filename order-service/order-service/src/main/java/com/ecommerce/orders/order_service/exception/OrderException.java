package com.ecommerce.orders.order_service.exception;

public class OrderException extends RuntimeException {

    public OrderException(String message) {
        super(message);
    }
}