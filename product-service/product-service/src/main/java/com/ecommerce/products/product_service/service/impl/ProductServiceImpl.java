package com.ecommerce.products.product_service.service.impl;

import com.ecommerce.products.product_service.dto.request.ProductRequest;
import com.ecommerce.products.product_service.dto.response.ProductResponse;
import com.ecommerce.products.product_service.entity.Category;
import com.ecommerce.products.product_service.entity.Product;
import com.ecommerce.products.product_service.exception.ResourceNotFoundException;
import com.ecommerce.products.product_service.repository.CategoryRepository;
import com.ecommerce.products.product_service.repository.ProductRepository;
import com.ecommerce.products.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found with id: "
                                                + request.getCategoryId()
                                )
                        );

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .price(request.getPrice())
                .build();

        Product savedProduct =
                productRepository.save(product);

        log.info(
                "Product created successfully. Product ID: {}",
                savedProduct.getId()
        );

        return mapToResponse(savedProduct);
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(product);
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Override
    @Transactional
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: "
                                                + id
                                )
                        );

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found with id: "
                                                + request.getCategoryId()
                                )
                        );

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setPrice(request.getPrice());

        Product updatedProduct =
                productRepository.save(product);

        log.info(
                "Product updated successfully. Product ID: {}",
                id
        );

        return mapToResponse(updatedProduct);
    }

    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @Override
    @Transactional
    public void deleteProduct(Long id) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: "
                                                + id
                                )
                        );

        productRepository.delete(product);

        log.info(
                "Product deleted successfully. Product ID: {}",
                id
        );
    }

    // =========================================================
    // MAP ENTITY -> RESPONSE
    // =========================================================

    private ProductResponse mapToResponse(Product product) {

        Category category = product.getCategory();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(
                        category != null
                                ? category.getId()
                                : null
                )
                .categoryName(
                        category != null
                                ? category.getName()
                                : null
                )
                .price(product.getPrice())
                .enabled(product.getEnabled())

                // =================================================
                // PRODUCT IMAGE URL
                // =================================================
                .imageUrl(product.getImageUrl())

                .build();
    }
}