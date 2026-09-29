package com.ecommerce.orders.order_service.client;

import com.ecommerce.orders.order_service.config.FeignConfig;
import com.ecommerce.orders.order_service.dto.request.PaymentRequest;
import com.ecommerce.orders.order_service.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "PAYMENT-SERVICE",
        configuration = FeignConfig.class
)
public interface PaymentClient {

    // =========================================================
    // CREATE PAYMENT
    // =========================================================

    @PostMapping("/api/payments")
    PaymentResponse createPayment(
            @RequestBody PaymentRequest request
    );


    // =========================================================
    // PROCESS PAYMENT
    // =========================================================

    @PostMapping("/api/payments/{id}/process")
    PaymentResponse processPayment(
            @PathVariable("id") Long paymentId
    );


    // =========================================================
    // GET PAYMENTS BY ORDER ID
    // =========================================================

    @GetMapping("/api/payments/order/{orderId}")
    List<PaymentResponse> getPaymentByOrderId(
            @PathVariable("orderId") Long orderId
    );
}
