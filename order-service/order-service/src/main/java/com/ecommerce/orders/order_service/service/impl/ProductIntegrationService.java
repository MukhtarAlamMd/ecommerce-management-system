package com.ecommerce.orders.order_service.service.impl;

import com.ecommerce.orders.order_service.client.ProductClient;
import com.ecommerce.orders.order_service.dto.response.ProductResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductIntegrationService {

    private final ProductClient productClient;

    @Retry(name = "productService")
    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "getProductFallback"
    )
    public ProductResponse getProductById(Long productId) {

        log.info(
                "Calling Product Service: productId={}",
                productId
        );

        return productClient.getProductById(productId);
    }

    private ProductResponse getProductFallback(
            Long productId,
            Throwable throwable) {

        log.error(
                "Product Service unavailable for productId={}",
                productId,
                throwable
        );

        throw new ProductServiceUnavailableException(
                "Product Service is currently unavailable. "
                        + "Please try again later."
        );
    }

    public static class ProductServiceUnavailableException
            extends RuntimeException {

        public ProductServiceUnavailableException(
                String message) {

            super(message);
        }
    }
}