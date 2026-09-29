package com.ecommerce.products.product_service.mapper;

import com.ecommerce.products.product_service.dto.request.CategoryRequest;
import com.ecommerce.products.product_service.dto.response.CategoryResponse;
import com.ecommerce.products.product_service.entity.Category;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    // =========================================================
    // REQUEST -> ENTITY
    // =========================================================

    public Category toEntity(CategoryRequest request) {

        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    public CategoryResponse toResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }


    // =========================================================
    // UPDATE ENTITY
    // =========================================================

    public void updateEntity(
            Category category,
            CategoryRequest request) {

        category.setName(request.getName());
        category.setDescription(request.getDescription());
    }
}

