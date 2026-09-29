package com.ecommerce.orders.order_service.controller;

import com.ecommerce.orders.order_service.dto.request.CreateOrderRequest;
import com.ecommerce.orders.order_service.dto.response.OrderResponse;
import com.ecommerce.orders.order_service.entity.OrderStatus;
import com.ecommerce.orders.order_service.service.OrderService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    // =========================================================
    // CREATE ORDER
    // CUSTOMER + ADMIN
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        String customerEmail = authentication.getName();

        OrderResponse response =
                orderService.createOrder(
                        request,
                        customerEmail
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET MY ORDERS
    // CUSTOMER + ADMIN
    // =========================================================

    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String customerEmail = authentication.getName();

        return ResponseEntity.ok(
                orderService.getOrdersByCustomerEmail(
                        customerEmail
                )
        );
    }


    // =========================================================
    // GET ALL ORDERS
    // ADMIN + SELLER
    //
    // NOTE:
    // Currently Order does not contain seller information.
    // Therefore SELLER will see all orders.
    // Later we can filter orders by seller.
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }


    // =========================================================
    // GET ORDER BY ID
    // CUSTOMER + ADMIN + SELLER
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SELLER')")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        OrderResponse order =
                orderService.getOrderById(id);


        // -----------------------------------------------------
        // ADMIN can view any order
        // -----------------------------------------------------

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(order);
        }


        // -----------------------------------------------------
        // SELLER can currently view order
        //
        // IMPORTANT:
        // Seller filtering will be added later when
        // seller ownership is stored in the order data.
        // -----------------------------------------------------

        boolean isSeller =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_SELLER"));

        if (isSeller) {
            return ResponseEntity.ok(order);
        }


        // -----------------------------------------------------
        // CUSTOMER can only view their own order
        // -----------------------------------------------------

        String customerEmail =
                authentication.getName();

        if (!customerEmail.equalsIgnoreCase(
                order.getCustomerEmail())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        return ResponseEntity.ok(order);
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // ADMIN + SELLER
    // =========================================================

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        status
                )
        );
    }


    // =========================================================
    // CANCEL ORDER
    // CUSTOMER + ADMIN
    // =========================================================

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        OrderResponse order =
                orderService.getOrderById(id);


        // -----------------------------------------------------
        // ADMIN can cancel any order
        // -----------------------------------------------------

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));


        // -----------------------------------------------------
        // CUSTOMER can cancel only their own order
        // -----------------------------------------------------

        if (!isAdmin) {

            String customerEmail =
                    authentication.getName();

            if (!customerEmail.equalsIgnoreCase(
                    order.getCustomerEmail())) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build();
            }
        }


        orderService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }
}