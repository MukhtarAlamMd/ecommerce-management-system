package com.ecommerce.inventory.inventory_service.mapper;

import com.ecommerce.inventory.inventory_service.dto.request.InventoryRequest;
import com.ecommerce.inventory.inventory_service.dto.response.InventoryResponse;
import com.ecommerce.inventory.inventory_service.entity.Inventory;

import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    // =========================================================
    // REQUEST → ENTITY
    // =========================================================

    public Inventory toEntity(InventoryRequest request) {

        return Inventory.builder()
                .productId(request.getProductId())
                .availableQuantity(
                        request.getAvailableQuantity()
                )
                .reservedQuantity(0)
                .reorderLevel(
                        request.getReorderLevel()
                )
                .enabled(true)
                .build();
    }

    // =========================================================
    // ENTITY → RESPONSE
    // =========================================================

    public InventoryResponse toResponse(
            Inventory inventory) {

        boolean lowStock =
                inventory.getAvailableQuantity()
                        <= inventory.getReorderLevel();

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .availableQuantity(
                        inventory.getAvailableQuantity()
                )
                .reservedQuantity(
                        inventory.getReservedQuantity()
                )
                .reorderLevel(
                        inventory.getReorderLevel()
                )
                .enabled(inventory.getEnabled())
                .lowStock(lowStock)
                .build();
    }

    // =========================================================
    // UPDATE ENTITY
    // =========================================================

    public void updateEntity(
            Inventory inventory,
            InventoryRequest request) {

        inventory.setProductId(
                request.getProductId()
        );

        inventory.setAvailableQuantity(
                request.getAvailableQuantity()
        );

        inventory.setReorderLevel(
                request.getReorderLevel()
        );
    }
}