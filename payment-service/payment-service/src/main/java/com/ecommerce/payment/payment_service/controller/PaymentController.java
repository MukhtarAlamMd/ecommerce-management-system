package com.ecommerce.payment.payment_service.controller;



import com.ecommerce.payment.payment_service.dto.PaymentRequest;
import com.ecommerce.payment.payment_service.dto.PaymentResponse;
import com.ecommerce.payment.payment_service.service.PaymentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    // =========================================================
    // CREATE PAYMENT
    // ADMIN / SELLER / CUSTOMER
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request));
    }


    // =========================================================
    // GET PAYMENT BY ID
    // ADMIN / SELLER / CUSTOMER
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(id)
        );
    }


    // =========================================================
    // GET ALL PAYMENTS
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }


    // =========================================================
    // GET PAYMENTS BY ORDER ID
    // ADMIN / SELLER / CUSTOMER
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByOrderId(orderId)
        );
    }


    // =========================================================
    // GET PAYMENTS BY USER ID
    // ADMIN / SELLER / CUSTOMER
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByUserId(userId)
        );
    }


    // =========================================================
    // PROCESS PAYMENT
    // ADMIN / SELLER / CUSTOMER
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.processPayment(id)
        );
    }


    // =========================================================
    // REFUND PAYMENT
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.refundPayment(id)
        );
    }


    // =========================================================
    // CANCEL PAYMENT
    // ADMIN / SELLER / CUSTOMER
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.cancelPayment(id)
        );
    }
}
