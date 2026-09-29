package com.ecommerce.products.product_service.controller;

import com.ecommerce.products.product_service.config.jwt.JwtService;
import com.ecommerce.products.product_service.dto.request.ProductRequest;
import com.ecommerce.products.product_service.dto.response.ProductResponse;
import com.ecommerce.products.product_service.service.CategoryService;
import com.ecommerce.products.product_service.service.ProductService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithMockUser(username = "admin", roles = "ADMIN")
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequest productRequest;

    private ProductResponse productResponse;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        productRequest = ProductRequest.builder()
                .name("Men's Cotton T-Shirt")
                .description("Premium cotton t-shirt")
                .categoryId(1L)
                .price(new BigDecimal("799.00"))
                .stockQuantity(10)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Men's Cotton T-Shirt")
                .description("Premium cotton t-shirt")
                .categoryId(1L)
                .price(new BigDecimal("799.00"))
                .stockQuantity(10)
                .build();
    }


    // =========================================================
    // CREATE PRODUCT - SUCCESS
    // =========================================================

    @Test
    void createProduct_shouldReturnCreated() throws Exception {

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(
                        post("/api/products")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                productRequest
                                        )
                                )
                )
                .andExpect(status().isCreated());

        verify(productService, times(1))
                .createProduct(any(ProductRequest.class));
    }


    // =========================================================
    // CREATE PRODUCT - NAME INVALID
    // =========================================================

    @Test
    void createProduct_shouldReturnBadRequest_whenNameIsBlank()
            throws Exception {

        ProductRequest invalidRequest = ProductRequest.builder()
                .name("")
                .description("Test")
                .categoryId(1L)
                .price(new BigDecimal("799.00"))
                .stockQuantity(10)
                .build();

        mockMvc.perform(
                        post("/api/products")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }


    // =========================================================
    // CREATE PRODUCT - CATEGORY NULL
    // =========================================================

    @Test
    void createProduct_shouldReturnBadRequest_whenCategoryIdIsNull()
            throws Exception {

        ProductRequest invalidRequest = ProductRequest.builder()
                .name("T-Shirt")
                .description("Test")
                .categoryId(null)
                .price(new BigDecimal("799.00"))
                .stockQuantity(10)
                .build();

        mockMvc.perform(
                        post("/api/products")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }


    // =========================================================
    // CREATE PRODUCT - CATEGORY ID ZERO
    // =========================================================

    @Test
    void createProduct_shouldReturnBadRequest_whenCategoryIdIsZero()
            throws Exception {

        ProductRequest invalidRequest = ProductRequest.builder()
                .name("T-Shirt")
                .description("Test")
                .categoryId(0L)
                .price(new BigDecimal("799.00"))
                .stockQuantity(10)
                .build();

        mockMvc.perform(
                        post("/api/products")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }


    // =========================================================
    // CREATE PRODUCT - PRICE ZERO
    // =========================================================

    @Test
    void createProduct_shouldReturnBadRequest_whenPriceIsZero()
            throws Exception {

        ProductRequest invalidRequest = ProductRequest.builder()
                .name("T-Shirt")
                .description("Test")
                .categoryId(1L)
                .price(BigDecimal.ZERO)
                .stockQuantity(10)
                .build();

        mockMvc.perform(
                        post("/api/products")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }


    // =========================================================
    // CREATE PRODUCT - NEGATIVE STOCK
    // =========================================================

    @Test
    void createProduct_shouldReturnBadRequest_whenStockIsNegative()
            throws Exception {

        ProductRequest invalidRequest = ProductRequest.builder()
                .name("T-Shirt")
                .description("Test")
                .categoryId(1L)
                .price(new BigDecimal("799.00"))
                .stockQuantity(-1)
                .build();

        mockMvc.perform(
                        post("/api/products")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Test
    void getAllProducts_shouldReturnOk() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(List.of(productResponse));

        mockMvc.perform(
                        get("/api/products")
                )
                .andExpect(status().isOk());

        verify(productService, times(1))
                .getAllProducts();
    }


    // =========================================================
    // GET ALL PRODUCTS - EMPTY
    // =========================================================

    @Test
    void getAllProducts_shouldReturnOk_whenEmpty() throws Exception {

        when(productService.getAllProducts())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/products")
                )
                .andExpect(status().isOk());

        verify(productService, times(1))
                .getAllProducts();
    }


    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @Test
    void getProductById_shouldReturnOk() throws Exception {

        when(productService.getProductById(1L))
                .thenReturn(productResponse);

        mockMvc.perform(
                        get("/api/products/1")
                )
                .andExpect(status().isOk());

        verify(productService, times(1))
                .getProductById(1L);
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Test
    void updateProduct_shouldReturnOk() throws Exception {

        when(productService.updateProduct(
                eq(1L),
                any(ProductRequest.class)
        )).thenReturn(productResponse);

        mockMvc.perform(
                        put("/api/products/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                productRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        verify(productService, times(1))
                .updateProduct(
                        eq(1L),
                        any(ProductRequest.class)
                );
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {

        mockMvc.perform(
                        delete("/api/products/1")
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        verify(productService, times(1))
                .deleteProduct(1L);
    }
}