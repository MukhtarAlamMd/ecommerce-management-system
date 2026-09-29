package com.ecommerce.products.product_service.mapper;

import com.ecommerce.products.product_service.dto.response.ProductResponse;
import com.ecommerce.products.product_service.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(
                        product.getCategory() != null
                                ? product.getCategory().getId()
                                : null
                )
                .categoryName(
                        product.getCategory() != null
                                ? product.getCategory().getName()
                                : null
                )
                .price(product.getPrice())
                //.stockQuantity(product.getStockQuantity())
                .enabled(product.getEnabled())
                .imageUrl(product.getImageUrl())
                .build();
    }
}