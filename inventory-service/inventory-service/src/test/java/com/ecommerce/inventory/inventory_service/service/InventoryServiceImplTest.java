package com.ecommerce.inventory.inventory_service.service;

import com.ecommerce.inventory.inventory_service.dto.request.InventoryRequest;
import com.ecommerce.inventory.inventory_service.dto.response.InventoryResponse;
import com.ecommerce.inventory.inventory_service.entity.Inventory;
import com.ecommerce.inventory.inventory_service.exception.DuplicateResourceException;
import com.ecommerce.inventory.inventory_service.exception.InsufficientStockException;
import com.ecommerce.inventory.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory.inventory_service.repository.InventoryRepository;

import com.ecommerce.inventory.inventory_service.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;


    private Inventory inventory;
    private InventoryRequest request;
    private InventoryResponse response;


    @BeforeEach
    void setUp() {

        request = new InventoryRequest();

        request.setProductId(1L);
        request.setAvailableQuantity(100);
        inventory = new Inventory();

        inventory.setId(1L);
        inventory.setProductId(1L);
        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(0);
        inventory.setEnabled(true);

        response = new InventoryResponse();
    }


    // =========================================================
    // CREATE INVENTORY
    // =========================================================

    @Test
    void createInventory_ShouldCreateSuccessfully() {

        when(inventoryRepository.existsByProductId(1L))
                .thenReturn(false);

        when(inventoryMapper.toEntity(request))
                .thenReturn(inventory);

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.createInventory(request);


        assertNotNull(result);
        assertEquals(response, result);

        verify(inventoryRepository)
                .existsByProductId(1L);

        verify(inventoryMapper)
                .toEntity(request);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // CREATE INVENTORY - DUPLICATE
    // =========================================================

    @Test
    void createInventory_ShouldThrowExceptionWhenAlreadyExists() {

        when(inventoryRepository.existsByProductId(1L))
                .thenReturn(true);


        assertThrows(
                DuplicateResourceException.class,
                () -> inventoryService.createInventory(request)
        );


        verify(inventoryRepository)
                .existsByProductId(1L);

        verify(inventoryMapper, never())
                .toEntity(any());

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // GET ALL INVENTORY
    // =========================================================

    @Test
    void getAllInventory_ShouldReturnAllInventory() {

        Inventory inventory2 = new Inventory();

        inventory2.setId(2L);
        inventory2.setProductId(2L);
        inventory2.setAvailableQuantity(50);
        inventory2.setReservedQuantity(0);
        inventory2.setEnabled(true);


        when(inventoryRepository.findAll())
                .thenReturn(List.of(inventory, inventory2));

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);

        InventoryResponse response2 =
                new InventoryResponse();

        when(inventoryMapper.toResponse(inventory2))
                .thenReturn(response2);


        List<InventoryResponse> result =
                inventoryService.getAllInventory();


        assertNotNull(result);
        assertEquals(2, result.size());

        verify(inventoryRepository)
                .findAll();

        verify(inventoryMapper)
                .toResponse(inventory);

        verify(inventoryMapper)
                .toResponse(inventory2);
    }


    // =========================================================
    // GET ALL INVENTORY - EMPTY
    // =========================================================

    @Test
    void getAllInventory_ShouldReturnEmptyList() {

        when(inventoryRepository.findAll())
                .thenReturn(List.of());


        List<InventoryResponse> result =
                inventoryService.getAllInventory();


        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(inventoryRepository)
                .findAll();
    }


    // =========================================================
    // GET INVENTORY BY ID
    // =========================================================

    @Test
    void getInventoryById_ShouldReturnInventory() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.getInventoryById(1L);


        assertNotNull(result);
        assertEquals(response, result);

        verify(inventoryRepository)
                .findById(1L);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // GET INVENTORY BY ID - NOT FOUND
    // =========================================================

    @Test
    void getInventoryById_ShouldThrowExceptionWhenNotFound() {

        when(inventoryRepository.findById(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.getInventoryById(99L)
        );


        verify(inventoryRepository)
                .findById(99L);

        verify(inventoryMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // GET INVENTORY BY PRODUCT ID
    // =========================================================

    @Test
    void getInventoryByProductId_ShouldReturnInventory() {

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.getInventoryByProductId(1L);


        assertNotNull(result);
        assertEquals(response, result);

        verify(inventoryRepository)
                .findByProductId(1L);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // GET INVENTORY BY PRODUCT ID - NOT FOUND
    // =========================================================

    @Test
    void getInventoryByProductId_ShouldThrowExceptionWhenNotFound() {

        when(inventoryRepository.findByProductId(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.getInventoryByProductId(99L)
        );


        verify(inventoryRepository)
                .findByProductId(99L);

        verify(inventoryMapper, never())
                .toResponse(any());
    }


    // =========================================================
    // UPDATE INVENTORY
    // =========================================================

    @Test
    void updateInventory_ShouldUpdateSuccessfully() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.updateInventory(
                        1L,
                        request
                );


        assertNotNull(result);
        assertEquals(response, result);

        verify(inventoryRepository)
                .findById(1L);

        verify(inventoryMapper)
                .updateEntity(inventory, request);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // UPDATE INVENTORY - NOT FOUND
    // =========================================================

    @Test
    void updateInventory_ShouldThrowExceptionWhenNotFound() {

        when(inventoryRepository.findById(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.updateInventory(
                        99L,
                        request
                )
        );


        verify(inventoryRepository)
                .findById(99L);

        verify(inventoryMapper, never())
                .updateEntity(any(), any());

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // UPDATE INVENTORY - DUPLICATE PRODUCT
    // =========================================================

    @Test
    void updateInventory_ShouldThrowDuplicateException() {

        InventoryRequest duplicateRequest =
                new InventoryRequest();

        duplicateRequest.setProductId(2L);
        duplicateRequest.setAvailableQuantity(50);
        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.existsByProductId(2L))
                .thenReturn(true);


        assertThrows(
                DuplicateResourceException.class,
                () -> inventoryService.updateInventory(
                        1L,
                        duplicateRequest
                )
        );


        verify(inventoryRepository)
                .findById(1L);

        verify(inventoryRepository)
                .existsByProductId(2L);

        verify(inventoryMapper, never())
                .updateEntity(any(), any());

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // DELETE INVENTORY
    // =========================================================

    @Test
    void deleteInventory_ShouldDeleteSuccessfully() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));


        assertDoesNotThrow(
                () -> inventoryService.deleteInventory(1L)
        );


        verify(inventoryRepository)
                .findById(1L);

        verify(inventoryRepository)
                .delete(inventory);
    }


    // =========================================================
    // DELETE INVENTORY - NOT FOUND
    // =========================================================

    @Test
    void deleteInventory_ShouldThrowExceptionWhenNotFound() {

        when(inventoryRepository.findById(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.deleteInventory(99L)
        );


        verify(inventoryRepository)
                .findById(99L);

        verify(inventoryRepository, never())
                .delete(any());
    }


    // =========================================================
    // ADD STOCK
    // =========================================================

    @Test
    void addStock_ShouldIncreaseAvailableQuantity() {

        inventory.setAvailableQuantity(100);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.addStock(1L, 20);


        assertNotNull(result);
        assertEquals(120, inventory.getAvailableQuantity());

        verify(inventoryRepository)
                .findByProductId(1L);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // ADD STOCK - INVALID QUANTITY
    // =========================================================

    @Test
    void addStock_ShouldThrowExceptionForInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.addStock(1L, 0)
        );

        verify(inventoryRepository, never())
                .findByProductId(anyLong());
    }


    @Test
    void addStock_ShouldThrowExceptionForNegativeQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.addStock(1L, -10)
        );

        verify(inventoryRepository, never())
                .findByProductId(anyLong());
    }


    @Test
    void addStock_ShouldThrowExceptionForNullQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.addStock(1L, null)
        );

        verify(inventoryRepository, never())
                .findByProductId(anyLong());
    }


    // =========================================================
    // ADD STOCK - PRODUCT NOT FOUND
    // =========================================================

    @Test
    void addStock_ShouldThrowExceptionWhenProductNotFound() {

        when(inventoryRepository.findByProductId(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.addStock(99L, 10)
        );


        verify(inventoryRepository)
                .findByProductId(99L);

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // REMOVE STOCK
    // =========================================================

    @Test
    void removeStock_ShouldDecreaseAvailableQuantity() {

        inventory.setAvailableQuantity(100);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.removeStock(1L, 30);


        assertNotNull(result);
        assertEquals(70, inventory.getAvailableQuantity());

        verify(inventoryRepository)
                .findByProductId(1L);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // REMOVE STOCK - INSUFFICIENT
    // =========================================================

    @Test
    void removeStock_ShouldThrowInsufficientStockException() {

        inventory.setAvailableQuantity(10);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));


        assertThrows(
                InsufficientStockException.class,
                () -> inventoryService.removeStock(1L, 20)
        );


        assertEquals(10, inventory.getAvailableQuantity());

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // REMOVE STOCK - INVALID QUANTITY
    // =========================================================

    @Test
    void removeStock_ShouldThrowExceptionForInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.removeStock(1L, 0)
        );

        verify(inventoryRepository, never())
                .findByProductId(anyLong());
    }


    // =========================================================
    // RESERVE STOCK
    // =========================================================

    @Test
    void reserveStock_ShouldMoveAvailableToReserved() {

        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(10);
        inventory.setEnabled(true);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.reserveStock(1L, 20);


        assertNotNull(result);

        assertEquals(
                80,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                30,
                inventory.getReservedQuantity()
        );


        verify(inventoryRepository)
                .findByProductId(1L);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // RESERVE STOCK - DISABLED
    // =========================================================

    @Test
    void reserveStock_ShouldThrowExceptionWhenDisabled() {

        inventory.setEnabled(false);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));


        assertThrows(
                IllegalStateException.class,
                () -> inventoryService.reserveStock(1L, 10)
        );


        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // RESERVE STOCK - INSUFFICIENT
    // =========================================================

    @Test
    void reserveStock_ShouldThrowInsufficientStockException() {

        inventory.setEnabled(true);
        inventory.setAvailableQuantity(5);
        inventory.setReservedQuantity(0);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));


        assertThrows(
                InsufficientStockException.class,
                () -> inventoryService.reserveStock(1L, 10)
        );


        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // RELEASE RESERVED STOCK
    // =========================================================

    @Test
    void releaseStock_ShouldMoveReservedToAvailable() {

        inventory.setAvailableQuantity(50);
        inventory.setReservedQuantity(30);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        when(inventoryMapper.toResponse(inventory))
                .thenReturn(response);


        InventoryResponse result =
                inventoryService.releaseStock(1L, 10);


        assertNotNull(result);

        assertEquals(
                60,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                20,
                inventory.getReservedQuantity()
        );


        verify(inventoryRepository)
                .findByProductId(1L);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryMapper)
                .toResponse(inventory);
    }


    // =========================================================
    // RELEASE RESERVED STOCK - TOO MUCH
    // =========================================================

    @Test
    void releaseStock_ShouldThrowExceptionWhenReservedStockInsufficient() {

        inventory.setAvailableQuantity(50);
        inventory.setReservedQuantity(5);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));


        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.releaseStock(1L, 10)
        );


        assertEquals(
                50,
                inventory.getAvailableQuantity()
        );

        assertEquals(
                5,
                inventory.getReservedQuantity()
        );

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // RELEASE STOCK - INVALID QUANTITY
    // =========================================================

    @Test
    void releaseStock_ShouldThrowExceptionForInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.releaseStock(1L, 0)
        );

        verify(inventoryRepository, never())
                .findByProductId(anyLong());
    }


    // =========================================================
    // REMOVE STOCK - PRODUCT NOT FOUND
    // =========================================================

    @Test
    void removeStock_ShouldThrowExceptionWhenProductNotFound() {

        when(inventoryRepository.findByProductId(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.removeStock(99L, 10)
        );


        verify(inventoryRepository)
                .findByProductId(99L);

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // RESERVE STOCK - PRODUCT NOT FOUND
    // =========================================================

    @Test
    void reserveStock_ShouldThrowExceptionWhenProductNotFound() {

        when(inventoryRepository.findByProductId(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.reserveStock(99L, 10)
        );


        verify(inventoryRepository)
                .findByProductId(99L);

        verify(inventoryRepository, never())
                .save(any());
    }


    // =========================================================
    // RELEASE STOCK - PRODUCT NOT FOUND
    // =========================================================

    @Test
    void releaseStock_ShouldThrowExceptionWhenProductNotFound() {

        when(inventoryRepository.findByProductId(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.releaseStock(99L, 10)
        );


        verify(inventoryRepository)
                .findByProductId(99L);

        verify(inventoryRepository, never())
                .save(any());
    }
}