package com.ecommerce.products.product_service.service.impl;

import com.ecommerce.products.product_service.dto.request.CategoryRequest;
import com.ecommerce.products.product_service.dto.response.CategoryResponse;
import com.ecommerce.products.product_service.entity.Category;
import com.ecommerce.products.product_service.mapper.CategoryMapper;
import com.ecommerce.products.product_service.repository.CategoryRepository;
import com.ecommerce.products.product_service.service.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;


    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    @Override
    public CategoryResponse createCategory(
            CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(
                request.getName())) {

            throw new RuntimeException(
                    "Category already exists with name: "
                            + request.getName()
            );
        }

        Category category =
                categoryMapper.toEntity(request);

        Category savedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }


    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: "
                                                + id
                                )
                        );

        return categoryMapper.toResponse(category);
    }


    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }


    // =========================================================
    // UPDATE CATEGORY
    // =========================================================

    @Override
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: "
                                                + id
                                )
                        );

        if (!category.getName()
                .equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(
                request.getName())) {

            throw new RuntimeException(
                    "Category already exists with name: "
                            + request.getName()
            );
        }

        categoryMapper.updateEntity(
                category,
                request
        );

        Category updatedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponse(
                updatedCategory
        );
    }


    // =========================================================
    // DELETE CATEGORY
    // =========================================================

    @Override
    public void deleteCategory(Long id) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: "
                                                + id
                                )
                        );

        categoryRepository.delete(category);
    }
}