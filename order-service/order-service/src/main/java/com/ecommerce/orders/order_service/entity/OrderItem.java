package com.ecommerce.orders.order_service.entity;


import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "order_items",
        indexes = {
                @Index(
                        name = "idx_order_item_order_id",
                        columnList = "order_id"
                ),
                @Index(
                        name = "idx_order_item_product_id",
                        columnList = "product_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================================
    // ORDER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;


    // =========================================================
    // PRODUCT
    // =========================================================

    @Column(
            name = "product_id",
            nullable = false
    )
    private Long productId;


    // Product name is stored as a snapshot.
    // Product may change later.
    @Column(
            nullable = false,
            length = 150
    )
    private String productName;


    // =========================================================
    // PRICE
    // =========================================================

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal price;


    // =========================================================
    // QUANTITY
    // =========================================================

    @Column(nullable = false)
    private Integer quantity;


    // =========================================================
    // SUBTOTAL
    // =========================================================

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal subtotal;
}