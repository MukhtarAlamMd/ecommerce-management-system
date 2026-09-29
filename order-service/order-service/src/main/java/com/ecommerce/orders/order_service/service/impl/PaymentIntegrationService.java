package com.ecommerce.orders.order_service.service.impl;

import com.ecommerce.orders.order_service.client.PaymentClient;
import com.ecommerce.orders.order_service.dto.request.PaymentRequest;
import com.ecommerce.orders.order_service.dto.response.PaymentResponse;
import com.ecommerce.orders.order_service.exception.PaymentServiceUnavailableException;

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.regex.Pattern;

/**
 * Handles all communication from Order Service to Payment Service.
 *
 * Important:
 * - Keycloak user ID is a String/UUID.
 * - Customer email is taken from the authenticated Keycloak JWT.
 * - An invalid email coming from an older/frontend flow is replaced by
 *   the verified email claim from Keycloak.
 * - HTTP 400 validation errors are NOT treated as "Payment Service unavailable".
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentIntegrationService {

    private final PaymentClient paymentClient;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            );

    // =========================================================
    // CREATE PAYMENT
    // =========================================================

    @Retry(name = "paymentService")
    @CircuitBreaker(
            name = "paymentService",
            fallbackMethod = "createPaymentFallback"
    )
    public PaymentResponse createPayment(
            PaymentRequest paymentRequest) {

        if (paymentRequest == null) {
            throw new IllegalArgumentException(
                    "Payment request cannot be null"
            );
        }

        // Always use the authenticated Keycloak email.
        // This prevents values such as username/display-name from
        // being sent to Payment Service as customerEmail.
        String authenticatedEmail =
                getAuthenticatedEmail();

        paymentRequest.setCustomerEmail(
                authenticatedEmail
        );

        log.info(
                "Calling Payment Service to create payment for orderId={}, customerEmail={}",
                paymentRequest.getOrderId(),
                authenticatedEmail
        );

        try {

            return paymentClient.createPayment(
                    paymentRequest
            );

        } catch (FeignException.BadRequest e) {

            // 400 means Payment Service received invalid data.
            // It is NOT a service outage, so do not convert it to
            // PaymentServiceUnavailableException.
            String response = e.contentUTF8();

            log.error(
                    "Payment Service rejected payment request. orderId={}, response={}",
                    paymentRequest.getOrderId(),
                    response
            );

            throw new IllegalArgumentException(
                    "Payment request rejected by Payment Service: "
                            + response,
                    e
            );
        }
    }

    // =========================================================
    // PROCESS PAYMENT
    // =========================================================

    @Retry(name = "paymentService")
    @CircuitBreaker(
            name = "paymentService",
            fallbackMethod = "processPaymentFallback"
    )
    public PaymentResponse processPayment(
            Long paymentId) {

        if (paymentId == null || paymentId <= 0) {
            throw new IllegalArgumentException(
                    "Payment ID must be greater than zero"
            );
        }

        log.info(
                "Calling Payment Service to process payment: paymentId={}",
                paymentId
        );

        return paymentClient.processPayment(
                paymentId
        );
    }

    // =========================================================
    // CREATE PAYMENT FALLBACK
    // =========================================================

    @SuppressWarnings("unused")
    private PaymentResponse createPaymentFallback(
            PaymentRequest paymentRequest,
            Throwable throwable) {

        log.error(
                "Payment Service unavailable while creating payment. "
                        + "orderId={}, reason={}",
                paymentRequest == null
                        ? null
                        : paymentRequest.getOrderId(),
                rootCauseMessage(throwable)
        );

        throw new PaymentServiceUnavailableException(
                "Payment Service is currently unavailable. "
                        + "Please try again later."
        );
    }

    // =========================================================
    // PROCESS PAYMENT FALLBACK
    // =========================================================

    @SuppressWarnings("unused")
    private PaymentResponse processPaymentFallback(
            Long paymentId,
            Throwable throwable) {

        log.error(
                "Payment Service unavailable while processing payment. "
                        + "paymentId={}, reason={}",
                paymentId,
                rootCauseMessage(throwable)
        );

        throw new PaymentServiceUnavailableException(
                "Payment Service is currently unavailable. "
                        + "Please try again later."
        );
    }

    // =========================================================
    // GET AUTHENTICATED KEYCLOAK EMAIL
    // =========================================================

    private String getAuthenticatedEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated user could not be determined"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof Jwt jwt)) {

            throw new IllegalStateException(
                    "Invalid JWT authentication"
            );
        }

        String email =
                jwt.getClaimAsString("email");

        if (email == null || email.isBlank()) {

            // Some Keycloak configurations may use
            // preferred_username as the email.
            String username =
                    jwt.getClaimAsString(
                            "preferred_username"
                    );

            if (isValidEmail(username)) {
                email = username;
            }
        }

        if (!isValidEmail(email)) {

            throw new IllegalStateException(
                    "A valid email address was not found "
                            + "in the Keycloak token"
            );
        }

        return email.trim();
    }

    // =========================================================
    // EMAIL VALIDATION
    // =========================================================

    private boolean isValidEmail(String email) {

        return email != null
                && !email.isBlank()
                && EMAIL_PATTERN
                .matcher(email.trim())
                .matches();
    }

    // =========================================================
    // ROOT CAUSE
    // =========================================================

    private String rootCauseMessage(
            Throwable throwable) {

        if (throwable == null) {
            return "Unknown error";
        }

        Throwable cause = throwable;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage() == null
                ? cause.getClass().getSimpleName()
                : cause.getMessage();
    }
}
