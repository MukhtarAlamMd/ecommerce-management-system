package com.ecommerce.orders.order_service.client;

import com.ecommerce.orders.order_service.config.FeignConfig;
import com.ecommerce.orders.order_service.dto.response.ProductResponse;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class
)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(
            @PathVariable("id") Long id
    );
}