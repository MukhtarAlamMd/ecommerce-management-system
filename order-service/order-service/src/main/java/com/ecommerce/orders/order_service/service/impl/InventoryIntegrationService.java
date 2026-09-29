package com.ecommerce.orders.order_service.service.impl;

import com.ecommerce.orders.order_service.client.InventoryClient;
import com.ecommerce.orders.order_service.dto.response.InventoryResponse;
import com.ecommerce.orders.order_service.exception.InventoryServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryIntegrationService {

    private final InventoryClient inventoryClient;

    private static final String INVENTORY_SERVICE = "inventoryService";

    // =========================================================
    // GET INVENTORY
    // =========================================================

    @Retry(name = INVENTORY_SERVICE)
    @CircuitBreaker(
            name = INVENTORY_SERVICE,
            fallbackMethod = "getInventoryByProductIdFallback"
    )
    public InventoryResponse getInventoryByProductId(
            Long productId
    ) {

        log.info(
                "Calling Inventory Service: productId={}",
                productId
        );

        return inventoryClient.getInventoryByProductId(productId);
    }


    // =========================================================
    // GET INVENTORY FALLBACK
    // =========================================================

    private InventoryResponse getInventoryByProductIdFallback(
            Long productId,
            Throwable throwable
    ) {

        log.error(
                "Inventory Service unavailable. productId={}",
                productId,
                throwable
        );

        throw new InventoryServiceUnavailableException(
                "Inventory Service is currently unavailable. " +
                        "Please try again later.",
                throwable
        );
    }


    // =========================================================
    // RESERVE STOCK
    // =========================================================

    @Retry(name = INVENTORY_SERVICE)
    @CircuitBreaker(
            name = INVENTORY_SERVICE,
            fallbackMethod = "reserveStockFallback"
    )
    public InventoryResponse reserveStock(
            Long productId,
            Integer quantity
    ) {

        log.info(
                "Calling Inventory Service to reserve stock: " +
                        "productId={}, quantity={}",
                productId,
                quantity
        );

        return inventoryClient.reserveStock(
                productId,
                quantity
        );
    }


    // =========================================================
    // RESERVE FALLBACK
    // =========================================================

    private InventoryResponse reserveStockFallback(
            Long productId,
            Integer quantity,
            Throwable throwable
    ) {

        log.error(
                "Inventory Service unavailable while reserving stock. " +
                        "productId={}, quantity={}",
                productId,
                quantity,
                throwable
        );

        throw new InventoryServiceUnavailableException(
                "Inventory Service is currently unavailable. " +
                        "Unable to reserve stock at this time.",
                throwable
        );
    }


    // =========================================================
    // RELEASE STOCK
    // =========================================================

    @Retry(name = INVENTORY_SERVICE)
    @CircuitBreaker(
            name = INVENTORY_SERVICE,
            fallbackMethod = "releaseStockFallback"
    )
    public InventoryResponse releaseStock(
            Long productId,
            Integer quantity
    ) {

        log.info(
                "Calling Inventory Service to release stock: " +
                        "productId={}, quantity={}",
                productId,
                quantity
        );

        return inventoryClient.releaseStock(
                productId,
                quantity
        );
    }


    // =========================================================
    // RELEASE FALLBACK
    // =========================================================

    private InventoryResponse releaseStockFallback(
            Long productId,
            Integer quantity,
            Throwable throwable
    ) {

        log.error(
                "Inventory Service unavailable while releasing stock. " +
                        "productId={}, quantity={}",
                productId,
                quantity,
                throwable
        );

        throw new InventoryServiceUnavailableException(
                "Inventory Service is currently unavailable. " +
                        "Unable to release stock at this time.",
                throwable
        );
    }
}