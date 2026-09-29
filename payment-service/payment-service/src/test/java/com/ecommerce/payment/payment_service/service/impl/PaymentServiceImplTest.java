package com.ecommerce.payment.payment_service.service.impl;

import com.ecommerce.payment.payment_service.dto.PaymentRequest;
import com.ecommerce.payment.payment_service.dto.PaymentResponse;
import com.ecommerce.payment.payment_service.entity.Payment;
import com.ecommerce.payment.payment_service.entity.PaymentMethod;
import com.ecommerce.payment.payment_service.entity.PaymentStatus;
import com.ecommerce.payment.payment_service.exception.PaymentProcessingException;
import com.ecommerce.payment.payment_service.exception.ResourceNotFoundException;
import com.ecommerce.payment.payment_service.mapper.PaymentMapper;
import com.ecommerce.payment.payment_service.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;

    private PaymentRequest paymentRequest;

    private PaymentResponse paymentResponse;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        paymentRequest = PaymentRequest.builder()
                .orderId(1L)
                .userId(10L)
                .customerEmail("customer@gmail.com")
                .amount(new BigDecimal("799.00"))
                .paymentMethod(PaymentMethod.UPI)
                .build();

        payment = Payment.builder()
                .id(1L)
                .orderId(1L)
                .userId(10L)
                .customerEmail("customer@gmail.com")
                .amount(new BigDecimal("799.00"))
                .paymentMethod(PaymentMethod.UPI)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentResponse = mock(PaymentResponse.class);
    }


    // =========================================================
    // CREATE PAYMENT
    // =========================================================

    @Test
    void createPayment_shouldCreatePaymentSuccessfully() {

        when(paymentMapper.toEntity(paymentRequest))
                .thenReturn(payment);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result =
                paymentService.createPayment(paymentRequest);

        assertNotNull(result);

        assertEquals(
                PaymentStatus.PENDING,
                payment.getPaymentStatus()
        );

        verify(paymentMapper)
                .toEntity(paymentRequest);

        verify(paymentRepository)
                .save(payment);

        verify(paymentMapper)
                .toResponse(payment);
    }


    // =========================================================
    // GET PAYMENT BY ID - SUCCESS
    // =========================================================

    @Test
    void getPaymentById_shouldReturnPaymentSuccessfully() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result =
                paymentService.getPaymentById(1L);

        assertNotNull(result);

        assertEquals(
                paymentResponse,
                result
        );

        verify(paymentRepository)
                .findById(1L);

        verify(paymentMapper)
                .toResponse(payment);
    }


    // =========================================================
    // GET PAYMENT BY ID - NOT FOUND
    // =========================================================

    @Test
    void getPaymentById_shouldThrowException_whenPaymentNotFound() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> paymentService.getPaymentById(999L)
                );

        assertEquals(
                "Payment not found with id: 999",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findById(999L);

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // GET ALL PAYMENTS - SUCCESS
    // =========================================================

    @Test
    void getAllPayments_shouldReturnAllPayments() {

        Payment payment2 = Payment.builder()
                .id(2L)
                .orderId(2L)
                .userId(20L)
                .customerEmail("user@gmail.com")
                .amount(new BigDecimal("999.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        PaymentResponse response2 =
                mock(PaymentResponse.class);

        when(paymentRepository.findAll())
                .thenReturn(List.of(payment, payment2));

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        when(paymentMapper.toResponse(payment2))
                .thenReturn(response2);

        List<PaymentResponse> result =
                paymentService.getAllPayments();

        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals(
                paymentResponse,
                result.get(0)
        );

        assertEquals(
                response2,
                result.get(1)
        );

        verify(paymentRepository)
                .findAll();

        verify(paymentMapper)
                .toResponse(payment);

        verify(paymentMapper)
                .toResponse(payment2);
    }


    // =========================================================
    // GET ALL PAYMENTS - EMPTY
    // =========================================================

    @Test
    void getAllPayments_shouldReturnEmptyList_whenNoPaymentsExist() {

        when(paymentRepository.findAll())
                .thenReturn(List.of());

        List<PaymentResponse> result =
                paymentService.getAllPayments();

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(paymentRepository)
                .findAll();

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // GET PAYMENTS BY ORDER ID
    // =========================================================

    @Test
    void getPaymentsByOrderId_shouldReturnPayments() {

        when(paymentRepository.findByOrderId(1L))
                .thenReturn(List.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        List<PaymentResponse> result =
                paymentService.getPaymentsByOrderId(1L);

        assertNotNull(result);

        assertEquals(1, result.size());

        assertEquals(
                paymentResponse,
                result.get(0)
        );

        verify(paymentRepository)
                .findByOrderId(1L);

        verify(paymentMapper)
                .toResponse(payment);
    }


    // =========================================================
    // GET PAYMENTS BY ORDER ID - EMPTY
    // =========================================================

    @Test
    void getPaymentsByOrderId_shouldReturnEmptyList_whenNoPaymentsExist() {

        when(paymentRepository.findByOrderId(999L))
                .thenReturn(List.of());

        List<PaymentResponse> result =
                paymentService.getPaymentsByOrderId(999L);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(paymentRepository)
                .findByOrderId(999L);

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // GET PAYMENTS BY USER ID
    // =========================================================

    @Test
    void getPaymentsByUserId_shouldReturnPayments() {

        when(paymentRepository.findByUserId(10L))
                .thenReturn(List.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        List<PaymentResponse> result =
                paymentService.getPaymentsByUserId(10L);

        assertNotNull(result);

        assertEquals(1, result.size());

        assertEquals(
                paymentResponse,
                result.get(0)
        );

        verify(paymentRepository)
                .findByUserId(10L);

        verify(paymentMapper)
                .toResponse(payment);
    }


    // =========================================================
    // GET PAYMENTS BY USER ID - EMPTY
    // =========================================================

    @Test
    void getPaymentsByUserId_shouldReturnEmptyList_whenNoPaymentsExist() {

        when(paymentRepository.findByUserId(999L))
                .thenReturn(List.of());

        List<PaymentResponse> result =
                paymentService.getPaymentsByUserId(999L);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(paymentRepository)
                .findByUserId(999L);

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // PROCESS PAYMENT - SUCCESS
    // =========================================================

    @Test
    void processPayment_shouldProcessPendingPaymentSuccessfully() {

        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result =
                paymentService.processPayment(1L);

        assertNotNull(result);

        assertEquals(
                PaymentStatus.SUCCESS,
                payment.getPaymentStatus()
        );

        assertNotNull(payment.getTransactionId());

        assertTrue(
                payment.getTransactionId()
                        .startsWith("TXN-")
        );

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository)
                .save(payment);

        verify(paymentMapper)
                .toResponse(payment);
    }


    // =========================================================
    // PROCESS PAYMENT - NOT FOUND
    // =========================================================

    @Test
    void processPayment_shouldThrowException_whenPaymentNotFound() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> paymentService.processPayment(999L)
                );

        assertEquals(
                "Payment not found with id: 999",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findById(999L);

        verify(paymentRepository, never())
                .save(any());

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // PROCESS PAYMENT - INVALID STATUS
    // =========================================================

    @Test
    void processPayment_shouldThrowException_whenPaymentIsNotPending() {

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentProcessingException exception =
                assertThrows(
                        PaymentProcessingException.class,
                        () -> paymentService.processPayment(1L)
                );

        assertEquals(
                "Payment cannot be processed because current status is: SUCCESS",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository, never())
                .save(any());

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // REFUND PAYMENT - SUCCESS
    // =========================================================

    @Test
    void refundPayment_shouldRefundSuccessfulPayment() {

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result =
                paymentService.refundPayment(1L);

        assertNotNull(result);

        assertEquals(
                PaymentStatus.REFUNDED,
                payment.getPaymentStatus()
        );

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository)
                .save(payment);

        verify(paymentMapper)
                .toResponse(payment);
    }


    // =========================================================
    // REFUND PAYMENT - NOT FOUND
    // =========================================================

    @Test
    void refundPayment_shouldThrowException_whenPaymentNotFound() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> paymentService.refundPayment(999L)
                );

        assertEquals(
                "Payment not found with id: 999",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findById(999L);

        verify(paymentRepository, never())
                .save(any());

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // REFUND PAYMENT - INVALID STATUS
    // =========================================================

    @Test
    void refundPayment_shouldThrowException_whenPaymentIsNotSuccessful() {

        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentProcessingException exception =
                assertThrows(
                        PaymentProcessingException.class,
                        () -> paymentService.refundPayment(1L)
                );

        assertEquals(
                "Only successful payments can be refunded",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository, never())
                .save(any());

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // CANCEL PAYMENT - SUCCESS
    // =========================================================

    @Test
    void cancelPayment_shouldCancelPendingPayment() {

        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(paymentResponse);

        PaymentResponse result =
                paymentService.cancelPayment(1L);

        assertNotNull(result);

        assertEquals(
                PaymentStatus.CANCELLED,
                payment.getPaymentStatus()
        );

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository)
                .save(payment);

        verify(paymentMapper)
                .toResponse(payment);
    }


    // =========================================================
    // CANCEL PAYMENT - NOT FOUND
    // =========================================================

    @Test
    void cancelPayment_shouldThrowException_whenPaymentNotFound() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> paymentService.cancelPayment(999L)
                );

        assertEquals(
                "Payment not found with id: 999",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findById(999L);

        verify(paymentRepository, never())
                .save(any());

        verify(paymentMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // CANCEL PAYMENT - INVALID STATUS
    // =========================================================

    @Test
    void cancelPayment_shouldThrowException_whenPaymentIsNotPending() {

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentProcessingException exception =
                assertThrows(
                        PaymentProcessingException.class,
                        () -> paymentService.cancelPayment(1L)
                );

        assertEquals(
                "Only pending payments can be cancelled",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findById(1L);

        verify(paymentRepository, never())
                .save(any());

        verify(paymentMapper, never())
                .toResponse(any());
    }
}

