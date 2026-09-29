package com.ecommerce.products.product_service.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private Long categoryId;

    private String categoryName;

    private BigDecimal price;

    private Boolean enabled;

    private String imageUrl;
}