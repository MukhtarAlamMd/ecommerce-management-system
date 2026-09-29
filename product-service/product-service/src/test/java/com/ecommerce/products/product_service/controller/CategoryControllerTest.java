package com.ecommerce.products.product_service.controller;

import com.ecommerce.products.product_service.config.jwt.JwtService;
import com.ecommerce.products.product_service.dto.request.CategoryRequest;
import com.ecommerce.products.product_service.dto.response.CategoryResponse;
import com.ecommerce.products.product_service.service.CategoryService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    /*
     * IMPORTANT:
     * Use @MockBean, NOT @Mock.
     *
     * @WebMvcTest loads Spring ApplicationContext.
     */



    /*
     * JwtAuthenticationFilter requires JwtService.
     * This mock allows the test ApplicationContext to start.
     */



    private CategoryRequest categoryRequest;

    private CategoryResponse categoryResponse;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        categoryRequest = CategoryRequest.builder()
                .name("Men's Clothing")
                .description("Men's clothing category")
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Men's Clothing")
                .description("Men's clothing category")
                .build();
    }


    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    @Test
    void createCategory_shouldReturnCreated() throws Exception {

        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenReturn(categoryResponse);

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                categoryRequest
                                        )
                                )
                )
                .andExpect(status().isCreated());

        verify(categoryService, times(1))
                .createCategory(any(CategoryRequest.class));
    }


    // =========================================================
    // CREATE CATEGORY - NULL NAME
    // =========================================================

    @Test
    void createCategory_shouldReturnBadRequest_whenNameIsNull()
            throws Exception {

        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name(null)
                .description("Test category")
                .build();

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(categoryService, times(0))
                .createCategory(any(CategoryRequest.class));
    }


    // =========================================================
    // CREATE CATEGORY - BLANK NAME
    // =========================================================

    @Test
    void createCategory_shouldReturnBadRequest_whenNameIsBlank()
            throws Exception {

        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name("   ")
                .description("Test category")
                .build();

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(categoryService, times(0))
                .createCategory(any(CategoryRequest.class));
    }


    // =========================================================
    // CREATE CATEGORY - NAME TOO LONG
    // =========================================================

    @Test
    void createCategory_shouldReturnBadRequest_whenNameTooLong()
            throws Exception {

        String longName = "A".repeat(101);

        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name(longName)
                .description("Test category")
                .build();

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(categoryService, times(0))
                .createCategory(any(CategoryRequest.class));
    }


    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    @Test
    void getAllCategories_shouldReturnOk() throws Exception {

        when(categoryService.getAllCategories())
                .thenReturn(List.of(categoryResponse));

        mockMvc.perform(
                        get("/api/categories")
                )
                .andExpect(status().isOk());

        verify(categoryService, times(1))
                .getAllCategories();
    }


    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================

    @Test
    void getCategoryById_shouldReturnOk() throws Exception {

        when(categoryService.getCategoryById(1L))
                .thenReturn(categoryResponse);

        mockMvc.perform(
                        get("/api/categories/1")
                )
                .andExpect(status().isOk());

        verify(categoryService, times(1))
                .getCategoryById(1L);
    }


    // =========================================================
    // UPDATE CATEGORY
    // =========================================================

    @Test
    void updateCategory_shouldReturnOk() throws Exception {

        when(categoryService.updateCategory(
                eq(1L),
                any(CategoryRequest.class)
        )).thenReturn(categoryResponse);

        mockMvc.perform(
                        put("/api/categories/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                categoryRequest
                                        )
                                )
                )
                .andExpect(status().isOk());

        verify(categoryService, times(1))
                .updateCategory(
                        eq(1L),
                        any(CategoryRequest.class)
                );
    }


    // =========================================================
    // UPDATE CATEGORY - INVALID NAME
    // =========================================================

    @Test
    void updateCategory_shouldReturnBadRequest_whenNameIsBlank()
            throws Exception {

        CategoryRequest invalidRequest = CategoryRequest.builder()
                .name("")
                .description("Updated description")
                .build();

        mockMvc.perform(
                        put("/api/categories/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(categoryService, times(0))
                .updateCategory(
                        eq(1L),
                        any(CategoryRequest.class)
                );
    }


    // =========================================================
    // DELETE CATEGORY
    // =========================================================

    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {

        mockMvc.perform(
                        delete("/api/categories/1")
                )
                .andExpect(status().isNoContent());

        verify(categoryService, times(1))
                .deleteCategory(1L);
    }
}