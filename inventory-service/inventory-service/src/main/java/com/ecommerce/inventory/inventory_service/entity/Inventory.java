package com.ecommerce.inventory.inventory_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product",
                        columnNames = "product_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================================
    // PRODUCT
    // =========================================================

    @Column(
            name = "product_id",
            nullable = false,
            unique = true
    )
    private Long productId;

    // =========================================================
    // AVAILABLE STOCK
    // =========================================================

    @Column(
            nullable = false
    )
    @Builder.Default
    private Integer availableQuantity = 0;

    // =========================================================
    // RESERVED STOCK
    // =========================================================

    @Column(
            nullable = false
    )
    @Builder.Default
    private Integer reservedQuantity = 0;

    // =========================================================
    // REORDER LEVEL
    // =========================================================

    @Column(
            nullable = false
    )
    @Builder.Default
    private Integer reorderLevel = 10;

    // =========================================================
    // STATUS
    // =========================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}