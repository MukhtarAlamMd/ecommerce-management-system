package com.ecommerce.payment.payment_service.service.impl;

//import com.ecommerce.orders.order_service.dto.request.PaymentRequest;

import com.ecommerce.payment.payment_service.dto.PaymentRequest;
import com.ecommerce.payment.payment_service.dto.PaymentResponse;
import com.ecommerce.payment.payment_service.dto.event.NotificationEvent;
import com.ecommerce.payment.payment_service.entity.Payment;
import com.ecommerce.payment.payment_service.entity.PaymentStatus;
import com.ecommerce.payment.payment_service.exception.PaymentProcessingException;
import com.ecommerce.payment.payment_service.exception.ResourceNotFoundException;
import com.ecommerce.payment.payment_service.mapper.PaymentMapper;
import com.ecommerce.payment.payment_service.repository.PaymentRepository;
import com.ecommerce.payment.payment_service.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OutboxEventService outboxEventService;

    @Override
    public PaymentResponse createPayment(@Valid PaymentRequest request) {

        Payment payment = paymentMapper.toEntity(request);

        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + id
                        )
                );

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {

        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUserId(String userId) {

        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse processPayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + id
                        )
                );

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentProcessingException(
                    "Payment cannot be processed because current status is: "
                            + payment.getPaymentStatus()
            );
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        payment.setTransactionId(
                "TXN-" + UUID.randomUUID()
        );

        Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    public PaymentResponse refundPayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + id
                        )
                );

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentProcessingException(
                    "Only successful payments can be refunded"
            );
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        Payment updatedPayment =
                paymentRepository.save(payment);

        NotificationEvent event =
                NotificationEvent.builder()
                        .userId(
                                String.valueOf(payment.getUserId())
                        )
                        .recipient(
                                payment.getCustomerEmail()
                        )
                        .orderId(
                                payment.getOrderId()
                        )
                        .type("PAYMENT_REFUNDED")
                        .title("Payment Refunded")
                        .message(
                                "Your payment of ₹"
                                        + payment.getAmount()
                                        + " for order #"
                                        + payment.getOrderId()
                                        + " has been refunded successfully."
                        )
                        .build();

        outboxEventService.saveNotificationEvent(event);

        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    public PaymentResponse cancelPayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + id
                        )
                );

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentProcessingException(
                    "Only pending payments can be cancelled"
            );
        }

        payment.setPaymentStatus(PaymentStatus.CANCELLED);

        Payment updatedPayment =
                paymentRepository.save(payment);

        return paymentMapper.toResponse(updatedPayment);
    }
}