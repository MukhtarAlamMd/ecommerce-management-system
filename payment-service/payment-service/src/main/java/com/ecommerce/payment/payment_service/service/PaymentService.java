package com.ecommerce.payment.payment_service.service;


import com.ecommerce.payment.payment_service.dto.PaymentRequest;
import com.ecommerce.payment.payment_service.dto.PaymentResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(@Valid PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByOrderId(Long orderId);

    List<PaymentResponse> getPaymentsByUserId(String userId);

    PaymentResponse processPayment(Long id);

    PaymentResponse refundPayment(Long id);

    PaymentResponse cancelPayment(Long id);
}