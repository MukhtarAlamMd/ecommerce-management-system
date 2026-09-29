package com.ecommerce.payment.payment_service.repository;

import com.ecommerce.payment.payment_service.entity.Payment;
import com.ecommerce.payment.payment_service.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByUserId(String userId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findByCustomerEmail(String customerEmail);
}