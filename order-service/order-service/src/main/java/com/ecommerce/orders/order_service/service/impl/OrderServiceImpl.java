package com.ecommerce.orders.order_service.service.impl;

import com.ecommerce.orders.order_service.dto.event.NotificationEvent;
import com.ecommerce.orders.order_service.dto.request.CreateOrderRequest;
import com.ecommerce.orders.order_service.dto.request.OrderItemRequest;
import com.ecommerce.orders.order_service.dto.request.PaymentRequest;
import com.ecommerce.orders.order_service.dto.response.InventoryResponse;
import com.ecommerce.orders.order_service.dto.response.OrderResponse;
import com.ecommerce.orders.order_service.dto.response.PaymentResponse;
import com.ecommerce.orders.order_service.dto.response.ProductResponse;
import com.ecommerce.orders.order_service.dto.response.UserResponse;

import com.ecommerce.orders.order_service.entity.Order;
import com.ecommerce.orders.order_service.entity.OrderItem;
import com.ecommerce.orders.order_service.entity.OrderStatus;

import com.ecommerce.orders.order_service.exception.InvalidOrderStatusException;
import com.ecommerce.orders.order_service.exception.ResourceNotFoundException;

import com.ecommerce.orders.order_service.mapper.OrderMapper;
import com.ecommerce.orders.order_service.repository.OrderRepository;
import com.ecommerce.orders.order_service.service.OrderService;
import com.ecommerce.orders.order_service.service.OutboxEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {



    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OutboxEventService outboxEventService;

    private final PaymentIntegrationService paymentIntegrationService;

    private final InventoryIntegrationService inventoryIntegrationService;

    private final ProductIntegrationService productIntegrationService;




    // =========================================================
    // CREATE ORDER
    // =========================================================

    @Override
    @Transactional(
            noRollbackFor = OrderPaymentException.class
    )
    public OrderResponse createOrder(
            CreateOrderRequest request,
            String customerEmail) {

        // =====================================================
        // VALIDATE REQUEST
        // =====================================================

        validateCreateOrderRequest(
                request,
                customerEmail
        );


        // =====================================================
        // GET AUTHENTICATED USER
        // =====================================================

        String userId = getCurrentUserId();


        // =====================================================
        // CREATE ORDER
        // =====================================================

        Order order = Order.builder()
                .customerEmail(customerEmail)
                .userId(userId)
                .shippingAddress(
                        request.getShippingAddress().trim()
                )
                .paymentMethod(
                        request.getPaymentMethod()
                )
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;


        // =====================================================
        // TRACK RESERVED ITEMS
        // =====================================================

        List<OrderItem> reservedItems =
                new ArrayList<>();


        // =====================================================
        // PROCESS ITEMS
        // =====================================================

        try {

            for (OrderItemRequest itemRequest :
                    request.getItems()) {

                if (itemRequest == null) {

                    throw new IllegalArgumentException(
                            "Order item cannot be null"
                    );
                }

                Long productId =
                        itemRequest.getProductId();

                Integer quantity =
                        itemRequest.getQuantity();


                // =================================================
                // VALIDATE PRODUCT ID
                // =================================================

                if (productId == null || productId <= 0) {

                    throw new IllegalArgumentException(
                            "Product ID must be greater than zero"
                    );
                }


                // =================================================
                // VALIDATE QUANTITY
                // =================================================

                if (quantity == null || quantity <= 0) {

                    throw new IllegalArgumentException(
                            "Quantity must be greater than zero "
                                    + "for product id: "
                                    + productId
                    );
                }


                // =================================================
                // GET PRODUCT
                // =================================================

                ProductResponse product =
                        productIntegrationService.getProductById(
                                productId
                        );


                if (product == null) {

                    throw new ResourceNotFoundException(
                            "Product not found with id: "
                                    + productId
                    );
                }


                // =================================================
                // VALIDATE PRICE
                // =================================================

                BigDecimal price =
                        product.getPrice();

                if (price == null
                        || price.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

                    throw new IllegalStateException(
                            "Invalid product price for product id: "
                                    + productId
                    );
                }


                // =================================================
                // GET INVENTORY
                // =================================================

                InventoryResponse inventory =
                        inventoryIntegrationService
                                .getInventoryByProductId(
                                        productId
                                );


                if (inventory == null) {

                    throw new ResourceNotFoundException(
                            "Inventory not found for product id: "
                                    + productId
                    );
                }


                // =================================================
                // CHECK INVENTORY ENABLED
                // =================================================

                if (!Boolean.TRUE.equals(
                        inventory.getEnabled()
                )) {

                    throw new IllegalStateException(
                            "Inventory is disabled for product id: "
                                    + productId
                    );
                }


                // =================================================
                // CHECK STOCK
                // =================================================

                Integer availableQuantity =
                        inventory.getAvailableQuantity();

                if (availableQuantity == null
                        || availableQuantity < quantity) {

                    throw new IllegalStateException(
                            "Insufficient stock for product id: "
                                    + productId
                                    + ". Available: "
                                    + availableQuantity
                                    + ", Requested: "
                                    + quantity
                    );
                }


                // =================================================
                // RESERVE STOCK
                // =================================================

                try {

                    inventoryIntegrationService.reserveStock(
                            productId,
                            quantity
                    );

                } catch (Exception e) {

                    throw new IllegalStateException(
                            "Unable to reserve stock for "
                                    + "product id: "
                                    + productId,
                            e
                    );
                }


                // =================================================
                // CALCULATE SUBTOTAL
                // =================================================

                BigDecimal subtotal =
                        price.multiply(
                                BigDecimal.valueOf(quantity)
                        );


                // =================================================
                // CREATE ORDER ITEM
                // =================================================

                OrderItem orderItem =
                        OrderItem.builder()
                                .order(order)
                                .productId(product.getId())
                                .productName(product.getName())
                                .price(price)
                                .quantity(quantity)
                                .subtotal(subtotal)
                                .build();

                order.getItems().add(orderItem);

                reservedItems.add(orderItem);

                totalAmount =
                        totalAmount.add(subtotal);
            }

        } catch (RuntimeException e) {

            // =================================================
            // COMPENSATE PARTIAL RESERVATIONS
            // =================================================

            releaseReservedStock(
                    reservedItems
            );

            throw e;
        }


        // =====================================================
        // SET TOTAL
        // =====================================================

        order.setTotalAmount(
                totalAmount
        );


        // =====================================================
        // SAVE ORDER
        // =====================================================

        Order savedOrder =
                orderRepository.save(order);


        // =====================================================
        // CREATE PAYMENT REQUEST
        // =====================================================

        PaymentRequest paymentRequest =
                PaymentRequest.builder()
                        .orderId(
                                savedOrder.getId()
                        )
                        .userId(userId)
                        .customerEmail(
                                savedOrder.getCustomerEmail()
                        )
                        .amount(
                                savedOrder.getTotalAmount()
                        )
                        .paymentMethod(
                                savedOrder.getPaymentMethod()
                        )
                        .build();


        // =====================================================
        // CREATE PAYMENT
        // =====================================================

        PaymentResponse paymentResponse;

        try {

            paymentResponse =
                    paymentIntegrationService.createPayment(
                            paymentRequest
                    );

        } catch (Exception e) {

            log.error(
                    "Payment creation failed for order {}",
                    savedOrder.getId(),
                    e
            );

            handlePaymentFailure(
                    savedOrder
            );

            throw new OrderPaymentException(
                    "Unable to create payment. "
                            + "Order cancelled.",
                    e
            );
        }


        // =====================================================
        // VALIDATE PAYMENT RESPONSE
        // =====================================================

        if (paymentResponse == null
                || paymentResponse.getId() == null) {

            handlePaymentFailure(
                    savedOrder
            );

            throw new OrderPaymentException(
                    "Invalid payment response. "
                            + "Order cancelled."
            );
        }


        // =====================================================
        // PROCESS PAYMENT
        // =====================================================

        PaymentResponse processedPayment;

        try {

            processedPayment =
                    paymentIntegrationService.processPayment(
                            paymentResponse.getId()
                    );

        } catch (Exception e) {

            log.error(
                    "Payment processing failed for order {}",
                    savedOrder.getId(),
                    e
            );

            handlePaymentFailure(
                    savedOrder
            );

            throw new OrderPaymentException(
                    "Unable to process payment: "
                            + rootCauseMessage(e)
                            + ". Order cancelled.",
                    e
            );
        }


        // =====================================================
        // VALIDATE PROCESSED PAYMENT
        // =====================================================

        if (processedPayment == null) {

            handlePaymentFailure(
                    savedOrder
            );

            throw new OrderPaymentException(
                    "Payment response is empty. "
                            + "Order cancelled."
            );
        }


        // =====================================================
        // CHECK PAYMENT STATUS
        // =====================================================

        if (processedPayment.getPaymentStatus() == null
                || !"SUCCESS".equalsIgnoreCase(
                String.valueOf(
                        processedPayment.getPaymentStatus()
                )
        )) {

            handlePaymentFailure(
                    savedOrder
            );

            throw new OrderPaymentException(
                    "Payment failed. Payment status: "
                            + processedPayment.getPaymentStatus()
            );
        }


        // =====================================================
        // PAYMENT SUCCESS
        // =====================================================

        savedOrder.setStatus(
                OrderStatus.CONFIRMED
        );

        Order confirmedOrder =
                orderRepository.save(
                        savedOrder
                );


        // =====================================================
        // SAVE ORDER CONFIRMED EVENT TO OUTBOX
        // =====================================================

        sendOrderCreatedNotification(
                confirmedOrder,
                userId,
                customerEmail
        );


        // =====================================================
        // RETURN
        // =====================================================

        return orderMapper.toResponse(
                confirmedOrder
        );
    }


    // =========================================================
    // VALIDATE CREATE ORDER REQUEST
    // =========================================================

    private void validateCreateOrderRequest(
            CreateOrderRequest request,
            String customerEmail) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Order request cannot be null"
            );
        }

        if (customerEmail == null
                || customerEmail.isBlank()) {

            throw new IllegalArgumentException(
                    "Customer email is required"
            );
        }

        if (request.getShippingAddress() == null
                || request.getShippingAddress().isBlank()) {

            throw new IllegalArgumentException(
                    "Shipping address is required"
            );
        }

        if (request.getPaymentMethod() == null) {

            throw new IllegalArgumentException(
                    "Payment method is required"
            );
        }

        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Order must contain at least one item"
            );
        }
    }


    // =========================================================
    // GET CURRENT USER ID
    // =========================================================
    private String getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Jwt jwt)) {

            throw new IllegalStateException(
                    "Invalid JWT authentication"
            );
        }

        String userId = jwt.getSubject();

        if (userId == null || userId.isBlank()) {

            throw new IllegalStateException(
                    "Authenticated user ID could not be determined"
            );
        }

        return userId;
    }
    // =========================================================
    // HANDLE PAYMENT FAILURE
    // =========================================================

    private void handlePaymentFailure(
            Order order) {

        if (order == null) {
            return;
        }


        // -----------------------------------------------------
        // CANCEL ORDER
        // -----------------------------------------------------

        order.setStatus(
                OrderStatus.CANCELLED
        );

        Order cancelledOrder =
                orderRepository.save(order);


        // -----------------------------------------------------
        // SAVE CANCELLED NOTIFICATION
        // -----------------------------------------------------

        sendOrderStatusNotification(
                cancelledOrder,
                OrderStatus.CANCELLED
        );


        // -----------------------------------------------------
        // RELEASE RESERVED STOCK
        // -----------------------------------------------------

        releaseReservedStock(
                cancelledOrder
        );
    }


    // =========================================================
    // RELEASE RESERVED STOCK - ORDER
    // =========================================================

    private void releaseReservedStock(
            Order order) {

        if (order == null) {
            return;
        }

        releaseReservedStock(
                order.getItems()
        );
    }


    // =========================================================
    // RELEASE RESERVED STOCK - ITEMS
    // =========================================================

    private void releaseReservedStock(
            List<OrderItem> items) {

        if (items == null
                || items.isEmpty()) {

            return;
        }

        for (OrderItem item : items) {

            if (item == null
                    || item.getProductId() == null
                    || item.getQuantity() == null) {

                continue;
            }

            try {

                inventoryIntegrationService.releaseStock(
                        item.getProductId(),
                        item.getQuantity()
                );

                log.info(
                        "Released {} reserved stock "
                                + "for product {}",
                        item.getQuantity(),
                        item.getProductId()
                );

            } catch (Exception e) {

                log.error(
                        "Unable to release reserved stock "
                                + "for product {}",
                        item.getProductId(),
                        e
                );
            }
        }
    }


    // =========================================================
    // SAVE ORDER CONFIRMED NOTIFICATION TO OUTBOX
    // =========================================================

    private void sendOrderCreatedNotification(
            Order order,
            String userId,
            String customerEmail) {

        NotificationEvent event =
                NotificationEvent.builder()
                        .userId(
                                String.valueOf(userId)
                        )
                        .recipient(customerEmail)
                        .orderId(order.getId())
                        .type("ORDER_CONFIRMED")
                        .title("Order Confirmed")
                        .message(
                                "Your order #"
                                        + order.getId()
                                        + " has been confirmed successfully."
                        )
                        .build();

        try {

            outboxEventService.saveNotificationEvent(
                    event
            );

            log.info(
                    "Order confirmation event saved to outbox "
                            + "for order {}",
                    order.getId()
            );

        } catch (Exception e) {

            log.error(
                    "Failed to save order confirmation event "
                            + "to outbox for order {}",
                    order.getId(),
                    e
            );

            throw e;
        }
    }


    // =========================================================
    // SAVE ORDER STATUS NOTIFICATION TO OUTBOX
    // =========================================================

    private void sendOrderStatusNotification(
            Order order,
            OrderStatus status) {

        if (order == null || status == null) {
            return;
        }

        // IMPORTANT:
        // Use the customer stored on the order.
        // Do NOT call getCurrentUserId() here because
        // an ADMIN or SELLER may be changing the status.

        if (order.getUserId() == null) {

            throw new IllegalStateException(
                    "User ID is missing for order: "
                            + order.getId()
            );
        }

        String type;
        String title;
        String message;

        switch (status) {

            case PROCESSING -> {

                type = "ORDER_PROCESSING";

                title = "Order Processing";

                message =
                        "Your order #"
                                + order.getId()
                                + " is now being processed.";
            }

            case SHIPPED -> {

                type = "ORDER_SHIPPED";

                title = "Order Shipped";

                message =
                        "Your order #"
                                + order.getId()
                                + " has been shipped.";
            }

            case DELIVERED -> {

                type = "ORDER_DELIVERED";

                title = "Order Delivered";

                message =
                        "Your order #"
                                + order.getId()
                                + " has been delivered successfully.";
            }

            case CANCELLED -> {

                type = "ORDER_CANCELLED";

                title = "Order Cancelled";

                message =
                        "Your order #"
                                + order.getId()
                                + " has been cancelled.";
            }

            default -> {
                return;
            }
        }


        // =====================================================
        // CREATE EVENT
        // =====================================================

        NotificationEvent event =
                NotificationEvent.builder()
                        .userId(
                                String.valueOf(
                                        order.getUserId()
                                )
                        )
                        .recipient(
                                order.getCustomerEmail()
                        )
                        .orderId(
                                order.getId()
                        )
                        .type(type)
                        .title(title)
                        .message(message)
                        .build();


        // =====================================================
        // SAVE EVENT TO OUTBOX
        // =====================================================

        try {

            outboxEventService.saveNotificationEvent(
                    event
            );

            log.info(
                    "Order status notification saved to outbox: "
                            + "orderId={}, userId={}, "
                            + "status={}, eventType={}",
                    order.getId(),
                    order.getUserId(),
                    status,
                    type
            );

        } catch (Exception e) {

            log.error(
                    "Failed to save order status notification "
                            + "to outbox: orderId={}, status={}",
                    order.getId(),
                    status,
                    e
            );

            throw e;
        }
    }


    // =========================================================
    // ROOT CAUSE MESSAGE
    // =========================================================

    private String rootCauseMessage(
            Exception exception) {

        if (exception == null) {
            return "Unknown error";
        }

        Throwable cause = exception;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage() == null
                ? cause.getClass().getSimpleName()
                : cause.getMessage();
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @Override
    public OrderResponse getOrderById(
            Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with id: "
                                                + id
                                )
                        );

        return orderMapper.toResponse(
                order
        );
    }


    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @Override
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }


    // =========================================================
    // GET CUSTOMER ORDERS
    // =========================================================

    @Override
    public List<OrderResponse>
    getOrdersByCustomerEmail(
            String customerEmail) {

        if (customerEmail == null
                || customerEmail.isBlank()) {

            throw new IllegalArgumentException(
                    "Customer email is required"
            );
        }

        return orderRepository
                .findByCustomerEmail(
                        customerEmail
                )
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long id,
            OrderStatus newStatus) {

        if (newStatus == null) {

            throw new InvalidOrderStatusException(
                    "Order status cannot be null"
            );
        }

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with id: "
                                                + id
                                )
                        );

        OrderStatus currentStatus =
                order.getStatus();


        // =====================================================
        // VALIDATE CURRENT STATUS
        // =====================================================

        if (currentStatus == null) {

            throw new InvalidOrderStatusException(
                    "Current order status is null"
            );
        }


        // =====================================================
        // TERMINAL STATES
        // =====================================================

        if (currentStatus == OrderStatus.CANCELLED) {

            throw new InvalidOrderStatusException(
                    "Cancelled order cannot be updated"
            );
        }

        if (currentStatus == OrderStatus.DELIVERED) {

            throw new InvalidOrderStatusException(
                    "Delivered order cannot be updated"
            );
        }


        // =====================================================
        // SAME STATUS
        // =====================================================

        if (currentStatus == newStatus) {

            throw new InvalidOrderStatusException(
                    "Order is already in "
                            + newStatus
                            + " status"
            );
        }


        // =====================================================
        // VALID STATUS TRANSITION
        // =====================================================

        boolean validTransition =
                switch (currentStatus) {

                    case PENDING ->
                            newStatus ==
                                    OrderStatus.CONFIRMED
                                    || newStatus ==
                                    OrderStatus.CANCELLED;

                    case CONFIRMED ->
                            newStatus ==
                                    OrderStatus.PROCESSING
                                    || newStatus ==
                                    OrderStatus.CANCELLED;

                    case PROCESSING ->
                            newStatus ==
                                    OrderStatus.SHIPPED;

                    case SHIPPED ->
                            newStatus ==
                                    OrderStatus.DELIVERED;

                    case DELIVERED,
                         CANCELLED ->
                            false;
                };


        if (!validTransition) {

            throw new InvalidOrderStatusException(
                    "Invalid order status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }


        // =====================================================
        // CANCEL
        // =====================================================

        if (newStatus ==
                OrderStatus.CANCELLED) {

            releaseReservedStock(order);
        }


        // =====================================================
        // SAVE
        // =====================================================

        order.setStatus(newStatus);

        Order updatedOrder =
                orderRepository.save(order);


        // =====================================================
        // SAVE STATUS NOTIFICATION TO OUTBOX
        // =====================================================

        sendOrderStatusNotification(
                updatedOrder,
                newStatus
        );


        // =====================================================
        // RETURN
        // =====================================================

        return orderMapper.toResponse(
                updatedOrder
        );
    }


    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Override
    @Transactional
    public void cancelOrder(
            Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with id: "
                                                + id
                                )
                        );

        OrderStatus currentStatus =
                order.getStatus();


        // =====================================================
        // ALREADY CANCELLED
        // =====================================================

        if (currentStatus ==
                OrderStatus.CANCELLED) {

            throw new InvalidOrderStatusException(
                    "Order is already cancelled"
            );
        }


        // =====================================================
        // DELIVERED
        // =====================================================

        if (currentStatus ==
                OrderStatus.DELIVERED) {

            throw new InvalidOrderStatusException(
                    "Delivered order cannot be cancelled"
            );
        }


        // =====================================================
        // ONLY PENDING / CONFIRMED
        // =====================================================

        if (currentStatus !=
                OrderStatus.PENDING
                && currentStatus !=
                OrderStatus.CONFIRMED) {

            throw new InvalidOrderStatusException(
                    "Order cannot be cancelled from status: "
                            + currentStatus
            );
        }


        // =====================================================
        // RELEASE STOCK
        // =====================================================

        releaseReservedStock(order);


        // =====================================================
        // CANCEL ORDER
        // =====================================================

        order.setStatus(
                OrderStatus.CANCELLED
        );

        Order cancelledOrder =
                orderRepository.save(order);


        // =====================================================
        // SAVE CANCELLED NOTIFICATION
        // =====================================================

        sendOrderStatusNotification(
                cancelledOrder,
                OrderStatus.CANCELLED
        );
    }


    // =========================================================
    // PAYMENT FAILURE EXCEPTION
    // =========================================================

    public static class OrderPaymentException
            extends RuntimeException {

        public OrderPaymentException(
                String message) {

            super(message);
        }

        public OrderPaymentException(
                String message,
                Throwable cause) {

            super(message, cause);
        }
    }
}