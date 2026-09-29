package com.ecommerce.payment.payment_service.dto;

import com.ecommerce.payment.payment_service.entity.PaymentMethod;
import com.ecommerce.payment.payment_service.entity.PaymentStatus;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;

    private Long orderId;

    // Keycloak UUID
    private String userId;

    private String customerEmail;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}