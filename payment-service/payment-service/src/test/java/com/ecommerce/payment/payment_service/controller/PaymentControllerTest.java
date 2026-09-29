package com.ecommerce.payment.payment_service.controller;

import com.ecommerce.payment.payment_service.dto.PaymentRequest;
import com.ecommerce.payment.payment_service.dto.PaymentResponse;
import com.ecommerce.payment.payment_service.entity.PaymentMethod;
import com.ecommerce.payment.payment_service.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.ecommerce.payment.payment_service.security.JwtService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PaymentService paymentService;

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

        paymentResponse = PaymentResponse.builder()
                .id(1L)
                .orderId(1L)
                .userId(10L)
                .customerEmail("customer@gmail.com")
                .amount(new BigDecimal("799.00"))
                .paymentMethod(PaymentMethod.UPI)
                .build();
    }


    // =========================================================
    // CREATE PAYMENT - SUCCESS
    // =========================================================

    @Test
    void createPayment_shouldReturnCreated() throws Exception {

        when(paymentService.createPayment(any(PaymentRequest.class)))
                .thenReturn(paymentResponse);

        mockMvc.perform(
                        post("/api/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                paymentRequest
                                        )
                                )
                )
                .andExpect(status().isCreated());

        verify(paymentService, times(1))
                .createPayment(any(PaymentRequest.class));
    }


    // =========================================================
    // CREATE PAYMENT - ORDER ID NULL
    // =========================================================

    @Test
    void createPayment_shouldReturnBadRequest_whenOrderIdIsNull()
            throws Exception {

        PaymentRequest invalidRequest = PaymentRequest.builder()
                .orderId(null)
                .userId(10L)
                .customerEmail("customer@gmail.com")
                .amount(new BigDecimal("799.00"))
                .paymentMethod(PaymentMethod.UPI)
                .build();

        mockMvc.perform(
                        post("/api/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(paymentService, times(0))
                .createPayment(any(PaymentRequest.class));
    }


    // =========================================================
    // CREATE PAYMENT - INVALID EMAIL
    // =========================================================

    @Test
    void createPayment_shouldReturnBadRequest_whenEmailIsInvalid()
            throws Exception {

        PaymentRequest invalidRequest = PaymentRequest.builder()
                .orderId(1L)
                .userId(10L)
                .customerEmail("invalid-email")
                .amount(new BigDecimal("799.00"))
                .paymentMethod(PaymentMethod.UPI)
                .build();

        mockMvc.perform(
                        post("/api/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(paymentService, times(0))
                .createPayment(any(PaymentRequest.class));
    }


    // =========================================================
    // CREATE PAYMENT - ZERO AMOUNT
    // =========================================================

    @Test
    void createPayment_shouldReturnBadRequest_whenAmountIsZero()
            throws Exception {

        PaymentRequest invalidRequest = PaymentRequest.builder()
                .orderId(1L)
                .userId(10L)
                .customerEmail("customer@gmail.com")
                .amount(BigDecimal.ZERO)
                .paymentMethod(PaymentMethod.UPI)
                .build();

        mockMvc.perform(
                        post("/api/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(paymentService, times(0))
                .createPayment(any(PaymentRequest.class));
    }


    // =========================================================
    // GET PAYMENT BY ID
    // =========================================================

    @Test
    void getPaymentById_shouldReturnOk() throws Exception {

        when(paymentService.getPaymentById(1L))
                .thenReturn(paymentResponse);

        mockMvc.perform(
                        get("/api/payments/1")
                )
                .andExpect(status().isOk());

        verify(paymentService, times(1))
                .getPaymentById(1L);
    }


    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    @Test
    void getAllPayments_shouldReturnOk() throws Exception {

        List<PaymentResponse> responses =
                List.of(paymentResponse);

        when(paymentService.getAllPayments())
                .thenReturn(responses);

        mockMvc.perform(
                        get("/api/payments")
                )
                .andExpect(status().isOk());

        verify(paymentService, times(1))
                .getAllPayments();
    }


    // =========================================================
    // GET PAYMENTS BY ORDER ID
    // =========================================================

    @Test
    void getPaymentsByOrderId_shouldReturnOk() throws Exception {

        List<PaymentResponse> responses =
                List.of(paymentResponse);

        when(paymentService.getPaymentsByOrderId(1L))
                .thenReturn(responses);

        mockMvc.perform(
                        get("/api/payments/order/1")
                )
                .andExpect(status().isOk());

        verify(paymentService, times(1))
                .getPaymentsByOrderId(1L);
    }


    // =========================================================
    // GET PAYMENTS BY USER ID
    // =========================================================

    @Test
    void getPaymentsByUserId_shouldReturnOk() throws Exception {

        List<PaymentResponse> responses =
                List.of(paymentResponse);

        when(paymentService.getPaymentsByUserId(10L))
                .thenReturn(responses);

        mockMvc.perform(
                        get("/api/payments/user/10")
                )
                .andExpect(status().isOk());

        verify(paymentService, times(1))
                .getPaymentsByUserId(10L);
    }


    // =========================================================
    // PROCESS PAYMENT
    // =========================================================

    @Test
    void processPayment_shouldReturnOk() throws Exception {

        when(paymentService.processPayment(1L))
                .thenReturn(paymentResponse);

        mockMvc.perform(
                        post("/api/payments/1/process")
                )
                .andExpect(status().isOk());

        verify(paymentService, times(1))
                .processPayment(1L);
    }


    // =========================================================
    // REFUND PAYMENT
    // =========================================================

    @Test
    void refundPayment_shouldReturnOk() throws Exception {

        when(paymentService.refundPayment(1L))
                .thenReturn(paymentResponse);

        mockMvc.perform(
                        patch("/api/payments/1/refund")
                )
                .andExpect(status().isOk());

        verify(paymentService, times(1))
                .refundPayment(1L);
    }


    // =========================================================
    // CANCEL PAYMENT
    // =========================================================

    @Test
    void cancelPayment_shouldReturnOk() throws Exception {

        when(paymentService.cancelPayment(1L))
                .thenReturn(paymentResponse);

        mockMvc.perform(
                        patch("/api/payments/1/cancel")
                )
                .andExpect(status().isOk());

        verify(paymentService, times(1))
                .cancelPayment(1L);
    }
}