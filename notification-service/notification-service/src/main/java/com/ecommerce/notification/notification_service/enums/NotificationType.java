package com.ecommerce.notification.notification_service.enums;

public enum NotificationType {

    ORDER_CREATED,
    ORDER_CONFIRMED,
    ORDER_PROCESSING,
    ORDER_CANCELLED,

    PAYMENT_SUCCESS,
    PAYMENT_FAILED,

    ORDER_SHIPPED,
    ORDER_DELIVERED,

    PAYMENT_REFUNDED,

    GENERAL
}