package com.ecommerce.orders.order_service.repository;

import com.ecommerce.orders.order_service.entity.Order;
import com.ecommerce.orders.order_service.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    // =========================================================
    // GET ALL ORDERS OF A CUSTOMER
    // =========================================================

    List<Order> findByCustomerEmail(
            String customerEmail
    );


    // =========================================================
    // GET ORDERS BY STATUS
    // =========================================================

    List<Order> findByStatus(
            OrderStatus status
    );


    // =========================================================
    // GET CUSTOMER ORDERS BY STATUS
    // =========================================================

    List<Order> findByCustomerEmailAndStatus(
            String customerEmail,
            OrderStatus status
    );
}