package com.ecommerce.orders.order_service.controller;
import com.ecommerce.orders.order_service.dto.request.CreateOrderRequest;
import com.ecommerce.orders.order_service.dto.request.OrderItemRequest;
import com.ecommerce.orders.order_service.dto.response.OrderResponse;
import com.ecommerce.orders.order_service.entity.OrderStatus;
import com.ecommerce.orders.order_service.entity.PaymentMethod;
import com.ecommerce.orders.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private OrderResponse orderResponse;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();

        objectMapper = new ObjectMapper();

        orderResponse = new OrderResponse();

        orderResponse.setId(1L);
        orderResponse.setCustomerEmail("customer@gmail.com");
        orderResponse.setStatus(OrderStatus.PENDING);
        orderResponse.setTotalAmount(
                new BigDecimal("1000.00")
        );
    }


    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Test
    void createOrder_ShouldReturn201() throws Exception {

        OrderItemRequest item =
                OrderItemRequest.builder()
                        .productId(1L)
                        .quantity(2)
                        .build();

        CreateOrderRequest request =
                CreateOrderRequest.builder()
                        .shippingAddress(
                                "Katihar, Bihar, India"
                        )
                        .paymentMethod(
                                PaymentMethod.COD
                        )
                        .items(List.of(item))
                        .build();

        when(authentication.getName())
                .thenReturn("customer@gmail.com");

        when(orderService.createOrder(
                any(CreateOrderRequest.class),
                eq("customer@gmail.com")
        )).thenReturn(orderResponse);

        mockMvc.perform(
                        post("/api/orders")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated());

        verify(orderService)
                .createOrder(
                        any(CreateOrderRequest.class),
                        eq("customer@gmail.com")
                );
    }


    // =========================================================
    // GET MY ORDERS
    // =========================================================

    @Test
    void getMyOrders_ShouldReturn200() throws Exception {

        when(authentication.getName())
                .thenReturn("customer@gmail.com");

        when(orderService.getOrdersByCustomerEmail(
                "customer@gmail.com"
        )).thenReturn(List.of(orderResponse));

        mockMvc.perform(
                        get("/api/orders/my-orders")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(orderService)
                .getOrdersByCustomerEmail(
                        "customer@gmail.com"
                );
    }


    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @Test
    void getAllOrders_ShouldReturn200() throws Exception {

        when(orderService.getAllOrders())
                .thenReturn(List.of(orderResponse));

        mockMvc.perform(
                        get("/api/orders")
                )
                .andExpect(status().isOk());

        verify(orderService)
                .getAllOrders();
    }


    // =========================================================
    // GET ORDER BY ID
    // CUSTOMER - OWNER
    // =========================================================

    @Test
    void getOrderById_ShouldReturn200() throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(orderResponse);

        doReturn(Collections.emptyList())
                .when(authentication)
                .getAuthorities();

        when(authentication.getName())
                .thenReturn("customer@gmail.com");

        mockMvc.perform(
                        get("/api/orders/1")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(orderService)
                .getOrderById(1L);
    }


    // =========================================================
    // GET ORDER BY ID
    // CUSTOMER - NOT OWNER
    // =========================================================

    @Test
    void getOrderById_ShouldReturn403_WhenCustomerIsNotOwner()
            throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(orderResponse);

        doReturn(Collections.emptyList())
                .when(authentication)
                .getAuthorities();

        when(authentication.getName())
                .thenReturn("another@gmail.com");

        mockMvc.perform(
                        get("/api/orders/1")
                                .principal(authentication)
                )
                .andExpect(status().isForbidden());

        verify(orderService)
                .getOrderById(1L);
    }


    // =========================================================
    // GET ORDER BY ID
    // ADMIN
    // =========================================================

    @Test
    void getOrderById_ShouldReturn200_WhenAdmin()
            throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(orderResponse);

        doReturn(
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                )
        )
                .when(authentication)
                .getAuthorities();

        mockMvc.perform(
                        get("/api/orders/1")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(orderService)
                .getOrderById(1L);
    }


    // =========================================================
    // GET ORDER BY ID
    // SELLER
    // =========================================================

    @Test
    void getOrderById_ShouldReturn200_WhenSeller()
            throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(orderResponse);

        doReturn(
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_SELLER"
                        )
                )
        )
                .when(authentication)
                .getAuthorities();

        mockMvc.perform(
                        get("/api/orders/1")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(orderService)
                .getOrderById(1L);
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Test
    void updateOrderStatus_ShouldReturn200()
            throws Exception {

        orderResponse.setStatus(
                OrderStatus.CONFIRMED
        );

        when(orderService.updateOrderStatus(
                1L,
                OrderStatus.CONFIRMED
        )).thenReturn(orderResponse);

        mockMvc.perform(
                        patch("/api/orders/1/status")
                                .param(
                                        "status",
                                        "CONFIRMED"
                                )
                )
                .andExpect(status().isOk());

        verify(orderService)
                .updateOrderStatus(
                        1L,
                        OrderStatus.CONFIRMED
                );
    }


    // =========================================================
    // CANCEL ORDER
    // CUSTOMER - OWNER
    // =========================================================

    @Test
    void cancelOrder_ShouldReturn204()
            throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(orderResponse);

        doReturn(Collections.emptyList())
                .when(authentication)
                .getAuthorities();

        when(authentication.getName())
                .thenReturn("customer@gmail.com");

        doNothing()
                .when(orderService)
                .cancelOrder(1L);

        mockMvc.perform(
                        patch("/api/orders/1/cancel")
                                .principal(authentication)
                )
                .andExpect(status().isNoContent());

        verify(orderService)
                .getOrderById(1L);

        verify(orderService)
                .cancelOrder(1L);
    }


    // =========================================================
    // CANCEL ORDER
    // CUSTOMER - NOT OWNER
    // =========================================================

    @Test
    void cancelOrder_ShouldReturn403_WhenCustomerIsNotOwner()
            throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(orderResponse);

        doReturn(Collections.emptyList())
                .when(authentication)
                .getAuthorities();

        when(authentication.getName())
                .thenReturn("another@gmail.com");

        mockMvc.perform(
                        patch("/api/orders/1/cancel")
                                .principal(authentication)
                )
                .andExpect(status().isForbidden());

        verify(orderService, never())
                .cancelOrder(1L);
    }


    // =========================================================
    // CANCEL ORDER
    // ADMIN
    // =========================================================

    @Test
    void cancelOrder_ShouldReturn204_WhenAdmin()
            throws Exception {

        when(orderService.getOrderById(1L))
                .thenReturn(orderResponse);

        doReturn(
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                )
        )
                .when(authentication)
                .getAuthorities();

        doNothing()
                .when(orderService)
                .cancelOrder(1L);

        mockMvc.perform(
                        patch("/api/orders/1/cancel")
                                .principal(authentication)
                )
                .andExpect(status().isNoContent());

        verify(orderService)
                .getOrderById(1L);

        verify(orderService)
                .cancelOrder(1L);
    }
}

