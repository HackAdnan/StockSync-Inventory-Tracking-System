package com.bazaar.inventorytrackingv2.controller;
import com.bazaar.inventorytrackingv2.dto.CreateStockMovementDTO;
import com.bazaar.inventorytrackingv2.dto.StockMovementDTO;
import com.bazaar.inventorytrackingv2.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @Autowired
    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public ResponseEntity<List<StockMovementDTO>> getAllMovements() {
        return ResponseEntity.ok(stockMovementService.getAllMovements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementDTO> getMovementById(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementService.getMovementById(id));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<StockMovementDTO>> getMovementsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByStore(storeId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementDTO>> getMovementsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByProduct(productId));
    }

    @GetMapping("/store/{storeId}/product/{productId}")
    public ResponseEntity<List<StockMovementDTO>> getMovementsByStoreAndProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(stockMovementService.getMovementsByStoreAndProduct(storeId, productId));
    }
    @GetMapping("/date-range")
    public ResponseEntity<List<StockMovementDTO>> getMovementsByDateRange(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(stockMovementService.getMovementsByDateRange(startDate, endDate));
    }

    @GetMapping("/store/{storeId}/date-range")
    public ResponseEntity<List<StockMovementDTO>> getMovementsByStoreAndDateRange(
            @PathVariable Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(stockMovementService.getMovementsByStoreAndDateRange(storeId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<StockMovementDTO> createStockMovement(@RequestBody CreateStockMovementDTO movementDTO) {
        return new ResponseEntity<>(stockMovementService.createStockMovement(movementDTO), HttpStatus.CREATED);
    }
}
