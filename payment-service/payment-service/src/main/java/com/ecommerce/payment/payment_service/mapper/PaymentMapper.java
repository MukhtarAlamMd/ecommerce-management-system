package com.ecommerce.payment.payment_service.mapper;

import com.ecommerce.payment.payment_service.dto.PaymentRequest;
import com.ecommerce.payment.payment_service.dto.PaymentResponse;
import com.ecommerce.payment.payment_service.entity.Payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request) {

        return Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .customerEmail(request.getCustomerEmail())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .customerEmail(payment.getCustomerEmail())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}