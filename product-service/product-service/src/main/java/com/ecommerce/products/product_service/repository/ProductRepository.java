package com.ecommerce.products.product_service.repository;

import com.ecommerce.products.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByEnabledTrue();

    List<Product> findByNameContainingIgnoreCase(String name);
}