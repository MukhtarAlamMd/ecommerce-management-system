package com.ecommerce.orders.order_service.mapper;

import com.ecommerce.orders.order_service.dto.response.OrderItemResponse;
import com.ecommerce.orders.order_service.dto.response.OrderResponse;
import com.ecommerce.orders.order_service.entity.Order;
import com.ecommerce.orders.order_service.entity.OrderItem;

import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    public OrderResponse toResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .customerEmail(order.getCustomerEmail())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(
                        order.getItems()
                                .stream()
                                .map(this::toItemResponse)
                                .toList()
                )
                .build();
    }

    // =========================================================
    // ITEM -> RESPONSE
    // =========================================================

    private OrderItemResponse toItemResponse(OrderItem item) {

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}