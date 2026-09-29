package com.ecommerce.orders.order_service.service;

import com.ecommerce.orders.order_service.dto.request.CreateOrderRequest;
import com.ecommerce.orders.order_service.dto.response.OrderResponse;
import com.ecommerce.orders.order_service.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(
            CreateOrderRequest request,
            String customerEmail
    );

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByCustomerEmail(
            String customerEmail
    );

    OrderResponse updateOrderStatus(
            Long id,
            OrderStatus status
    );

    void cancelOrder(Long id);
}