package com.ecommerce.products.product_service.service;

import com.ecommerce.products.product_service.dto.request.ProductRequest;
import com.ecommerce.products.product_service.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(
            ProductRequest request
    );

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(
            Long id
    );

    ProductResponse updateProduct(
            Long id,
            ProductRequest request
    );

    void deleteProduct(
            Long id
    );
}