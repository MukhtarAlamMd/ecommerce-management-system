package com.ecommerce.orders.order_service.client;

import com.ecommerce.orders.order_service.config.FeignConfig;
import com.ecommerce.orders.order_service.dto.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "INVENTORY-SERVICE",
        configuration = FeignConfig.class
)
public interface InventoryClient {

    // =========================================================
    // GET INVENTORY BY PRODUCT
    // =========================================================

    @GetMapping("/api/inventory/product/{productId}")
    InventoryResponse getInventoryByProductId(
            @PathVariable("productId") Long productId
    );


    // =========================================================
    // RESERVE STOCK
    // =========================================================

    @PostMapping("/api/inventory/{productId}/reserve")
    InventoryResponse reserveStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );


    // =========================================================
    // RELEASE RESERVED STOCK
    // =========================================================

    @PostMapping("/api/inventory/{productId}/release")
    InventoryResponse releaseStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );
}