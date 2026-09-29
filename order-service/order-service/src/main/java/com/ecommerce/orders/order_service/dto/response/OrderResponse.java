package com.ecommerce.orders.order_service.dto.response;

import com.ecommerce.orders.order_service.entity.OrderStatus;
import com.ecommerce.orders.order_service.entity.PaymentMethod;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    // Keycloak UUID
    private String userId;

    private String customerEmail;

    private String shippingAddress;

    private PaymentMethod paymentMethod;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;
}