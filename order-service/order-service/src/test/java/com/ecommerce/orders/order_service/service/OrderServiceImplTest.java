package com.ecommerce.orders.order_service.service;

import com.ecommerce.orders.order_service.client.InventoryClient;
import com.ecommerce.orders.order_service.client.PaymentClient;
import com.ecommerce.orders.order_service.client.ProductClient;

import com.ecommerce.orders.order_service.dto.request.CreateOrderRequest;
import com.ecommerce.orders.order_service.dto.request.OrderItemRequest;
import com.ecommerce.orders.order_service.dto.request.PaymentRequest;

import com.ecommerce.orders.order_service.dto.response.InventoryResponse;
import com.ecommerce.orders.order_service.dto.response.OrderResponse;
import com.ecommerce.orders.order_service.dto.response.PaymentResponse;
import com.ecommerce.orders.order_service.dto.response.ProductResponse;

import com.ecommerce.orders.order_service.entity.Order;
import com.ecommerce.orders.order_service.entity.OrderItem;
import com.ecommerce.orders.order_service.entity.OrderStatus;
import com.ecommerce.orders.order_service.entity.PaymentMethod;
import com.ecommerce.orders.order_service.entity.PaymentStatus;

import com.ecommerce.orders.order_service.exception.InvalidOrderStatusException;
import com.ecommerce.orders.order_service.exception.ResourceNotFoundException;

import com.ecommerce.orders.order_service.mapper.OrderMapper;
import com.ecommerce.orders.order_service.repository.OrderRepository;
import com.ecommerce.orders.order_service.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductClient productClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderResponse orderResponse;
    private CreateOrderRequest createOrderRequest;
    private ProductResponse productResponse;
    private InventoryResponse inventoryResponse;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {

        // =====================================================
        // ORDER
        // =====================================================

        order = Order.builder()
                .id(1L)
                .customerEmail("customer@gmail.com")
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        orderResponse = new OrderResponse();


        // =====================================================
        // CREATE ORDER REQUEST
        // =====================================================

        createOrderRequest = new CreateOrderRequest();

        OrderItemRequest itemRequest =
                new OrderItemRequest();

        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        createOrderRequest.setShippingAddress(
                "Patna, Bihar, India"
        );

        createOrderRequest.setPaymentMethod(
                PaymentMethod.CARD
        );

        createOrderRequest.setItems(
                List.of(itemRequest)
        );


        // =====================================================
        // PRODUCT
        // =====================================================

        productResponse = new ProductResponse();

        productResponse.setId(1L);

        productResponse.setName(
                "Test Product"
        );

        productResponse.setPrice(
                BigDecimal.valueOf(100)
        );


        // =====================================================
        // INVENTORY
        // =====================================================

        inventoryResponse =
                new InventoryResponse();

        inventoryResponse.setEnabled(true);

        inventoryResponse.setAvailableQuantity(10);


        // =====================================================
        // PAYMENT
        // =====================================================

        paymentResponse =
                new PaymentResponse();

        paymentResponse.setId(500L);

        paymentResponse.setPaymentStatus(
                PaymentStatus.SUCCESS
        );
    }
}