package com.ecommerce.inventory.inventory_service.controller;

import com.ecommerce.inventory.inventory_service.dto.request.InventoryRequest;
import com.ecommerce.inventory.inventory_service.dto.response.InventoryResponse;
import com.ecommerce.inventory.inventory_service.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private InventoryService inventoryService;

    private InventoryRequest request;
    private InventoryResponse response;

    @BeforeEach
    void setUp() {

        InventoryController controller =
                new InventoryController(inventoryService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        objectMapper = new ObjectMapper();

        request = new InventoryRequest();
        request.setProductId(1L);
        request.setAvailableQuantity(100);

        response = new InventoryResponse();
    }

    // ...keep the remaining tests exactly as you have them
}