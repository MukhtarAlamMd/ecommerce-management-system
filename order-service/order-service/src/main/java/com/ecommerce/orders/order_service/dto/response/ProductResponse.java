package com.ecommerce.orders.order_service.dto.response;

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

    private BigDecimal price;

    private Integer stockQuantity;
}