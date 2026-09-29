package com.ecommerce.inventory.inventory_service.controller;

import com.ecommerce.inventory.inventory_service.dto.request.InventoryRequest;
import com.ecommerce.inventory.inventory_service.dto.response.InventoryResponse;
import com.ecommerce.inventory.inventory_service.service.InventoryService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    // =========================================================
    // CREATE INVENTORY
    // ADMIN + SELLER
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody InventoryRequest request) {

        InventoryResponse response =
                inventoryService.createInventory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET ALL INVENTORY
    // ADMIN + SELLER + CUSTOMER
    // =========================================================

    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {

        return ResponseEntity.ok(
                inventoryService.getAllInventory()
        );
    }


    // =========================================================
    // GET INVENTORY BY ID
    // ADMIN + SELLER + CUSTOMER
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    public ResponseEntity<InventoryResponse> getInventoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                inventoryService.getInventoryById(id)
        );
    }


    // =========================================================
    // GET INVENTORY BY PRODUCT ID
    // ADMIN + SELLER + CUSTOMER
    // =========================================================

    @GetMapping("/product/{productId}")
  //  @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProductId(productId)
        );
    }


    // =========================================================
    // UPDATE INVENTORY
    // ADMIN + SELLER
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.updateInventory(
                        id,
                        request
                )
        );
    }


    // =========================================================
    // DELETE INVENTORY
    // ADMIN ONLY
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInventory(
            @PathVariable Long id) {

        inventoryService.deleteInventory(id);

        return ResponseEntity.noContent().build();
    }


    // =========================================================
    // ADD STOCK
    // ADMIN + SELLER
    // =========================================================

    @PostMapping("/{productId}/stock-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<InventoryResponse> addStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.addStock(
                        productId,
                        quantity
                )
        );
    }


    // =========================================================
    // RESERVE STOCK
    //
    // CUSTOMER + ADMIN + SELLER
    //
    // IMPORTANT:
    // Order Service calls this during checkout.
    // =========================================================

    @PostMapping("/{productId}/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.reserveStock(
                        productId,
                        quantity
                )
        );
    }


    // =========================================================
    // RELEASE RESERVED STOCK
    //
    // CUSTOMER + ADMIN + SELLER
    // =========================================================

    @PostMapping("/{productId}/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.releaseStock(
                        productId,
                        quantity
                )
        );
    }


    // =========================================================
    // REMOVE STOCK
    // ADMIN + SELLER
    // =========================================================

    @PostMapping("/{productId}/remove")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<InventoryResponse> removeStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.removeStock(
                        productId,
                        quantity
                )
        );
    }
}