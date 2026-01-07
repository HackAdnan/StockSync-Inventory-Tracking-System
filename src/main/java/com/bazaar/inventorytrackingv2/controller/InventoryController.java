package com.bazaar.inventorytrackingv2.controller;
import com.bazaar.inventorytrackingv2.dto.InventoryDTO;
import com.bazaar.inventorytrackingv2.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    //  User can view all inventory
    @GetMapping("/user/all")
    public ResponseEntity<List<InventoryDTO>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    //  Get inventory by product and store
    @GetMapping("/user/product/{productId}/store/{storeId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductAndStore(
            @PathVariable Long productId,
            @PathVariable Long storeId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductAndStore(productId, storeId));
    }

    //  User can view inventory by store
    @GetMapping("/user/store/{storeId}")
    public ResponseEntity<List<InventoryDTO>> getInventoryByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(inventoryService.getInventoryByStore(storeId));
    }

    //  Admin-only: Modify inventory
    @PutMapping("/admin/product/{productId}/store/{storeId}")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable Long productId,
            @PathVariable Long storeId,
            @RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.ok(inventoryService.updateInventory(productId, storeId, inventoryDTO));
    }

    //  User can check low stock items
    @GetMapping("/user/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }
}
