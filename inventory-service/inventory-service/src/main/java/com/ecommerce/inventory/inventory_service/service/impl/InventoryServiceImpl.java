package com.ecommerce.inventory.inventory_service.service.impl;

import com.ecommerce.inventory.inventory_service.dto.request.InventoryRequest;
import com.ecommerce.inventory.inventory_service.dto.response.InventoryResponse;
import com.ecommerce.inventory.inventory_service.entity.Inventory;
import com.ecommerce.inventory.inventory_service.exception.DuplicateResourceException;
import com.ecommerce.inventory.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory.inventory_service.service.InventoryService;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;


    // =========================================================
    // CREATE INVENTORY
    // ADMIN + SELLER
    // =========================================================

    @Override
    @Transactional
    public InventoryResponse createInventory(
            InventoryRequest request) {

        validateProductId(request.getProductId());
        validateQuantity(request.getAvailableQuantity());

        if (inventoryRepository.existsByProductId(
                request.getProductId())) {

            throw new DuplicateResourceException(
                    "Inventory already exists for product id: "
                            + request.getProductId()
            );
        }

        Integer reorderLevel = request.getReorderLevel();

        if (reorderLevel == null || reorderLevel < 0) {
            reorderLevel = 0;
        }

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .availableQuantity(request.getAvailableQuantity())
                .reservedQuantity(0)
                .reorderLevel(reorderLevel)
                .enabled(true)
                .build();

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        log.info(
                "Inventory created successfully. Inventory ID: {}, Product ID: {}, Quantity: {}",
                savedInventory.getId(),
                savedInventory.getProductId(),
                savedInventory.getAvailableQuantity()
        );

        return inventoryMapper.toResponse(savedInventory);
    }


    // =========================================================
    // GET ALL INVENTORY
    // =========================================================

    @Override
    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }


    // =========================================================
    // GET INVENTORY BY ID
    // =========================================================

    @Override
    public InventoryResponse getInventoryById(Long id) {

        Inventory inventory =
                inventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found with id: "
                                                + id
                                )
                        );

        return inventoryMapper.toResponse(inventory);
    }


    // =========================================================
    // GET INVENTORY BY PRODUCT ID
    // =========================================================

    @Override
    public InventoryResponse getInventoryByProductId(
            Long productId) {

        validateProductId(productId);

        Inventory inventory =
                inventoryRepository
                        .findByProductId(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found for product id: "
                                                + productId
                                )
                        );

        return inventoryMapper.toResponse(inventory);
    }


    // =========================================================
    // UPDATE INVENTORY
    // =========================================================

    @Override
    @Transactional
    public InventoryResponse updateInventory(
            Long id,
            InventoryRequest request) {

        validateProductId(request.getProductId());
        validateQuantity(request.getAvailableQuantity());

        Inventory inventory =
                inventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found with id: "
                                                + id
                                )
                        );

        // Product ID must never change
        if (!inventory.getProductId()
                .equals(request.getProductId())) {

            throw new IllegalArgumentException(
                    "Product ID cannot be changed for existing inventory"
            );
        }

        int reservedQuantity =
                inventory.getReservedQuantity() == null
                        ? 0
                        : inventory.getReservedQuantity();

        /*
         * Available stock cannot be lower than zero.
         */
        if (request.getAvailableQuantity() < 0) {

            throw new IllegalArgumentException(
                    "Available quantity cannot be negative"
            );
        }

        inventory.setAvailableQuantity(
                request.getAvailableQuantity()
        );

        inventory.setReorderLevel(
                request.getReorderLevel() == null
                        ? 0
                        : request.getReorderLevel()
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        log.info(
                "Inventory updated. ID: {}, Product ID: {}, Available: {}, Reserved: {}",
                id,
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                reservedQuantity
        );

        return inventoryMapper.toResponse(updatedInventory);
    }


    // =========================================================
    // DELETE INVENTORY
    // =========================================================

    @Override
    @Transactional
    public void deleteInventory(Long id) {

        Inventory inventory =
                inventoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found with id: "
                                                + id
                                )
                        );

        inventoryRepository.delete(inventory);

        log.info(
                "Inventory deleted. ID: {}, Product ID: {}",
                id,
                inventory.getProductId()
        );
    }


    // =========================================================
    // ADD STOCK
    // =========================================================

    @Override
    @Transactional
    public InventoryResponse addStock(
            Long productId,
            Integer quantity) {

        validateProductId(productId);
        validateQuantity(quantity);

        Inventory inventory =
                getInventoryEntityByProductId(productId);

        ensureEnabled(inventory);

        int available =
                getAvailableQuantity(inventory);

        inventory.setAvailableQuantity(
                available + quantity
        );

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        log.info(
                "Added {} units to product {}. Available stock: {}",
                quantity,
                productId,
                savedInventory.getAvailableQuantity()
        );

        return inventoryMapper.toResponse(savedInventory);
    }


    // =========================================================
    // RESERVE STOCK
    //
    // available -> reserved
    // =========================================================

    @Override
    @Transactional
    public InventoryResponse reserveStock(
            Long productId,
            Integer quantity) {

        validateProductId(productId);
        validateQuantity(quantity);

        Inventory inventory =
                getInventoryEntityByProductId(productId);

        ensureEnabled(inventory);

        int available =
                getAvailableQuantity(inventory);

        int reserved =
                getReservedQuantity(inventory);

        // -----------------------------------------------------
        // CHECK AVAILABLE STOCK
        // -----------------------------------------------------

        if (available < quantity) {

            throw new IllegalStateException(
                    "Insufficient stock for product id: "
                            + productId
                            + ". Available: "
                            + available
                            + ", Requested: "
                            + quantity
            );
        }

        // -----------------------------------------------------
        // MOVE AVAILABLE -> RESERVED
        // -----------------------------------------------------

        inventory.setAvailableQuantity(
                available - quantity
        );

        inventory.setReservedQuantity(
                reserved + quantity
        );

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        log.info(
                "Reserved {} units for product {}. Available: {}, Reserved: {}",
                quantity,
                productId,
                savedInventory.getAvailableQuantity(),
                savedInventory.getReservedQuantity()
        );

        return inventoryMapper.toResponse(savedInventory);
    }


    // =========================================================
    // RELEASE RESERVED STOCK
    //
    // reserved -> available
    // =========================================================

    @Override
    @Transactional
    public InventoryResponse releaseStock(
            Long productId,
            Integer quantity) {

        validateProductId(productId);
        validateQuantity(quantity);

        Inventory inventory =
                getInventoryEntityByProductId(productId);

        ensureEnabled(inventory);

        int available =
                getAvailableQuantity(inventory);

        int reserved =
                getReservedQuantity(inventory);

        // -----------------------------------------------------
        // CHECK RESERVED STOCK
        // -----------------------------------------------------

        if (reserved < quantity) {

            throw new IllegalStateException(
                    "Cannot release more stock than reserved "
                            + "for product id: "
                            + productId
                            + ". Reserved: "
                            + reserved
                            + ", Requested: "
                            + quantity
            );
        }

        // -----------------------------------------------------
        // MOVE RESERVED -> AVAILABLE
        // -----------------------------------------------------

        inventory.setReservedQuantity(
                reserved - quantity
        );

        inventory.setAvailableQuantity(
                available + quantity
        );

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        log.info(
                "Released {} units for product {}. Available: {}, Reserved: {}",
                quantity,
                productId,
                savedInventory.getAvailableQuantity(),
                savedInventory.getReservedQuantity()
        );

        return inventoryMapper.toResponse(savedInventory);
    }


    // =========================================================
    // REMOVE STOCK
    // =========================================================

    @Override
    @Transactional
    public InventoryResponse removeStock(
            Long productId,
            Integer quantity) {

        validateProductId(productId);
        validateQuantity(quantity);

        Inventory inventory =
                getInventoryEntityByProductId(productId);

        ensureEnabled(inventory);

        int available =
                getAvailableQuantity(inventory);

        if (available < quantity) {

            throw new IllegalStateException(
                    "Insufficient stock for product id: "
                            + productId
                            + ". Available: "
                            + available
                            + ", Requested: "
                            + quantity
            );
        }

        inventory.setAvailableQuantity(
                available - quantity
        );

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        log.info(
                "Removed {} units from product {}. Remaining: {}",
                quantity,
                productId,
                savedInventory.getAvailableQuantity()
        );

        return inventoryMapper.toResponse(savedInventory);
    }


    // =========================================================
    // FIND INVENTORY ENTITY
    // =========================================================

    private Inventory getInventoryEntityByProductId(
            Long productId) {

        return inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product id: "
                                        + productId
                        )
                );
    }


    // =========================================================
    // VALIDATE PRODUCT ID
    // =========================================================

    private void validateProductId(Long productId) {

        if (productId == null || productId <= 0) {

            throw new IllegalArgumentException(
                    "Product ID must be greater than zero"
            );
        }
    }


    // =========================================================
    // VALIDATE QUANTITY
    // =========================================================

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }


    // =========================================================
    // GET AVAILABLE QUANTITY
    // =========================================================

    private int getAvailableQuantity(
            Inventory inventory) {

        return inventory.getAvailableQuantity() == null
                ? 0
                : inventory.getAvailableQuantity();
    }


    // =========================================================
    // GET RESERVED QUANTITY
    // =========================================================

    private int getReservedQuantity(
            Inventory inventory) {

        return inventory.getReservedQuantity() == null
                ? 0
                : inventory.getReservedQuantity();
    }


    // =========================================================
    // CHECK INVENTORY ENABLED
    // =========================================================

    private void ensureEnabled(
            Inventory inventory) {

        if (!Boolean.TRUE.equals(
                inventory.getEnabled())) {

            throw new IllegalStateException(
                    "Inventory is disabled for product id: "
                            + inventory.getProductId()
            );
        }
    }
}