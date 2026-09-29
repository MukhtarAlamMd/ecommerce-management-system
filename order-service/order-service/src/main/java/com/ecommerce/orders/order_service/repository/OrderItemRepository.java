package com.ecommerce.orders.order_service.repository;
import com.ecommerce.orders.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // Get all items belonging to an order
    List<OrderItem> findByOrderId(Long orderId);

    // Find order items for a product
    List<OrderItem> findByProductId(Long productId);
}