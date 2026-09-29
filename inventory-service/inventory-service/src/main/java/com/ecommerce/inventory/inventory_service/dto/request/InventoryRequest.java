package com.ecommerce.inventory.inventory_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be greater than 0")
    private Long productId;

    @NotNull(message = "Available quantity is required")
    @Min(
            value = 0,
            message = "Available quantity cannot be negative"
    )
    private Integer availableQuantity;

    @NotNull(message = "Reorder level is required")
    @Min(
            value = 0,
            message = "Reorder level cannot be negative"
    )
    private Integer reorderLevel;
}