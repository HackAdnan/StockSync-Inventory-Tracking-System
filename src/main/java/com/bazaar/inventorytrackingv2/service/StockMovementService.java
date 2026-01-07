package com.bazaar.inventorytrackingv2.service;
import com.bazaar.inventorytrackingv2.exception.BadRequestException;
import com.bazaar.inventorytrackingv2.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import  com.bazaar.inventorytrackingv2.repository.*;
import com.bazaar.inventorytrackingv2.dto.*;
import com.bazaar.inventorytrackingv2.model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public StockMovementService(StockMovementRepository stockMovementRepository,
                                ProductRepository productRepository,
                                StoreRepository storeRepository,
                                InventoryRepository inventoryRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getAllMovements() {
        return stockMovementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StockMovementDTO getMovementById(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with id: " + id));
        return convertToDTO(movement);
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        return stockMovementRepository.findByStore(store).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return stockMovementRepository.findByProduct(product).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByStoreAndProduct(Long storeId, Long productId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return stockMovementRepository.findByStoreAndProduct(store, product).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return stockMovementRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByStoreAndDateRange(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        return stockMovementRepository.findByStoreAndDateRange(store, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockMovementDTO createStockMovement(CreateStockMovementDTO movementDTO) {
        Product product = productRepository.findById(movementDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + movementDTO.getProductId()));

        Store store = storeRepository.findById(movementDTO.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + movementDTO.getStoreId()));

        // Validate movement quantity
        if (movementDTO.getQuantity() <= 0 && movementDTO.getType() == MovementType.STOCK_IN) {
            throw new BadRequestException("Stock-in quantity must be positive");
        }

        if (movementDTO.getQuantity() >= 0 && (movementDTO.getType() == MovementType.SALE ||
                movementDTO.getType() == MovementType.MANUAL_REMOVAL)) {
            throw new BadRequestException("Sale or removal quantity must be negative");
        }

        // Create the movement
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setStore(store);
        movement.setType(movementDTO.getType());
        movement.setQuantity(movementDTO.getQuantity());
        movement.setReference(movementDTO.getReference());
        movement.setNotes(movementDTO.getNotes());
        movement.setTimestamp(LocalDateTime.now());
        movement.setCreatedBy(movementDTO.getCreatedBy());

        StockMovement savedMovement = stockMovementRepository.save(movement);

        // Update inventory
        updateInventory(product, store, movementDTO.getQuantity());

        return convertToDTO(savedMovement);
    }

    private void updateInventory(Product product, Store store, Integer quantityChange) {
        Inventory inventory = inventoryRepository.findByProductAndStore(product, store)
                .orElse(new Inventory());

        if (inventory.getQuantity() == null) {
            inventory.setQuantity(0);
        }

        inventory.setProduct(product);
        inventory.setStore(store);
        inventory.setQuantity(inventory.getQuantity() + quantityChange);
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRepository.save(inventory);
    }

    private StockMovementDTO convertToDTO(StockMovement movement) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setProductId(movement.getProduct().getId());
        dto.setProductSku(movement.getProduct().getSku());
        dto.setProductName(movement.getProduct().getName());
        dto.setStoreId(movement.getStore().getId());
        dto.setStoreCode(movement.getStore().getCode());
        dto.setStoreName(movement.getStore().getName());
        dto.setType(movement.getType());
        dto.setQuantity(movement.getQuantity());
        dto.setReference(movement.getReference());
        dto.setNotes(movement.getNotes());
        dto.setTimestamp(movement.getTimestamp());
        dto.setCreatedBy(movement.getCreatedBy());
        return dto;
    }
}
