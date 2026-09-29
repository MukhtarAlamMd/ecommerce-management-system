package com.ecommerce.orders.order_service.feign;

import com.ecommerce.orders.order_service.config.FeignConfig;
import com.ecommerce.orders.order_service.dto.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "inventory-service",
        configuration = FeignConfig.class
)
public interface InventoryServiceClient {

    @GetMapping("/api/inventory/product/{productId}")
    InventoryResponse getInventoryByProductId(
            @PathVariable("productId") Long productId
    );

    @PostMapping("/api/inventory/{productId}/reserve")
    InventoryResponse reserveStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );

    @PostMapping("/api/inventory/{productId}/release")
    InventoryResponse releaseStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") Integer quantity
    );
}