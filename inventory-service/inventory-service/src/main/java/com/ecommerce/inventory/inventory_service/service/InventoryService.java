package com.ecommerce.inventory.inventory_service.service;

import com.ecommerce.inventory.inventory_service.dto.request.InventoryRequest;
import com.ecommerce.inventory.inventory_service.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(
            InventoryRequest request
    );

    List<InventoryResponse> getAllInventory();

    InventoryResponse getInventoryById(Long id);

    InventoryResponse getInventoryByProductId(Long productId);

    InventoryResponse updateInventory(
            Long id,
            InventoryRequest request
    );

    void deleteInventory(Long id);

    InventoryResponse addStock(
            Long productId,
            Integer quantity
    );

    InventoryResponse removeStock(
            Long productId,
            Integer quantity
    );

    InventoryResponse reserveStock(
            Long productId,
            Integer quantity
    );

    InventoryResponse releaseStock(
            Long productId,
            Integer quantity
    );
}