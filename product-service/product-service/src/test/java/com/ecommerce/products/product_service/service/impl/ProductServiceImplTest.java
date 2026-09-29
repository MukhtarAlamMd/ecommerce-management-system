package com.ecommerce.products.product_service.service.impl;
import com.ecommerce.products.product_service.dto.request.ProductRequest;
import com.ecommerce.products.product_service.dto.response.ProductResponse;
import com.ecommerce.products.product_service.entity.Category;
import com.ecommerce.products.product_service.entity.Product;
import com.ecommerce.products.product_service.exception.ResourceNotFoundException;
import com.ecommerce.products.product_service.repository.CategoryRepository;
import com.ecommerce.products.product_service.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {

        category = Category.builder()
                .id(1L)
                .name("Men")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Men's Cotton T-Shirt")
                .description("Premium cotton t-shirt")
                .category(category)
                .price(new BigDecimal("799.00"))
                .stockQuantity(10)
                .build();

        productRequest = ProductRequest.builder()
                .name("Men's Cotton T-Shirt")
                .description("Premium cotton t-shirt")
                .categoryId(1L)
                .price(new BigDecimal("799.00"))
                .stockQuantity(10)
                .build();
    }

    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @Test
    void createProduct_shouldCreateSuccessfully() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response =
                productService.createProduct(productRequest);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("Men's Cotton T-Shirt", response.getName());
        assertEquals("Premium cotton t-shirt", response.getDescription());
        assertEquals(1L, response.getCategoryId());
        assertEquals("Men", response.getCategoryName());
        assertEquals(
                new BigDecimal("799.00"),
                response.getPrice()
        );
        assertEquals(10, response.getStockQuantity());

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .save(any(Product.class));
    }

    // =========================================================
    // CREATE PRODUCT - CATEGORY NOT FOUND
    // =========================================================

    @Test
    void createProduct_shouldThrowException_whenCategoryNotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.createProduct(productRequest)
                );

        assertEquals(
                "Category not found with id: 1",
                exception.getMessage()
        );

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, never())
                .save(any(Product.class));
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Test
    void getAllProducts_shouldReturnProducts() {

        when(productRepository.findAll())
                .thenReturn(List.of(product));

        List<ProductResponse> responses =
                productService.getAllProducts();

        assertNotNull(responses);

        assertEquals(1, responses.size());

        assertEquals(
                1L,
                responses.get(0).getId()
        );

        assertEquals(
                "Men's Cotton T-Shirt",
                responses.get(0).getName()
        );

        verify(productRepository, times(1))
                .findAll();
    }

    // =========================================================
    // GET ALL PRODUCTS - EMPTY
    // =========================================================

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNoProducts() {

        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> responses =
                productService.getAllProducts();

        assertNotNull(responses);

        assertTrue(responses.isEmpty());

        verify(productRepository, times(1))
                .findAll();
    }

    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @Test
    void getProductById_shouldReturnProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productService.getProductById(1L);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals(
                "Men's Cotton T-Shirt",
                response.getName()
        );
        assertEquals(1L, response.getCategoryId());

        verify(productRepository, times(1))
                .findById(1L);
    }

    // =========================================================
    // GET PRODUCT BY ID - NOT FOUND
    // =========================================================

    @Test
    void getProductById_shouldThrowException_whenProductNotFound() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.getProductById(99L)
                );

        assertEquals(
                "Product not found with id: 99",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .findById(99L);
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Test
    void updateProduct_shouldUpdateSuccessfully() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response =
                productService.updateProduct(
                        1L,
                        productRequest
                );

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals(
                "Men's Cotton T-Shirt",
                response.getName()
        );
        assertEquals(1L, response.getCategoryId());

        verify(productRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .save(product);
    }

    // =========================================================
    // UPDATE PRODUCT - PRODUCT NOT FOUND
    // =========================================================

    @Test
    void updateProduct_shouldThrowException_whenProductNotFound() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.updateProduct(
                                99L,
                                productRequest
                        )
                );

        assertEquals(
                "Product not found with id: 99",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .findById(99L);

        verify(categoryRepository, never())
                .findById(anyLong());

        verify(productRepository, never())
                .save(any(Product.class));
    }

    // =========================================================
    // UPDATE PRODUCT - CATEGORY NOT FOUND
    // =========================================================

    @Test
    void updateProduct_shouldThrowException_whenCategoryNotFound() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        ProductRequest request =
                ProductRequest.builder()
                        .name("Updated T-Shirt")
                        .description("Updated description")
                        .categoryId(99L)
                        .price(new BigDecimal("899.00"))
                        .stockQuantity(20)
                        .build();

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.updateProduct(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Category not found with id: 99",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .findById(99L);

        verify(productRepository, never())
                .save(any(Product.class));
    }

    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @Test
    void deleteProduct_shouldDeleteSuccessfully() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        doNothing()
                .when(productRepository)
                .delete(product);

        productService.deleteProduct(1L);

        verify(productRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .delete(product);
    }

    // =========================================================
    // DELETE PRODUCT - NOT FOUND
    // =========================================================

    @Test
    void deleteProduct_shouldThrowException_whenProductNotFound() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> productService.deleteProduct(99L)
                );

        assertEquals(
                "Product not found with id: 99",
                exception.getMessage()
        );

        verify(productRepository, times(1))
                .findById(99L);

        verify(productRepository, never())
                .delete(any(Product.class));
    }
}