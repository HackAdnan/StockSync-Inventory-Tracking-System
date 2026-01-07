package com.bazaar.inventorytrackingv2.service;
import com.bazaar.inventorytrackingv2.exception.ResourceNotFoundException;
import com.bazaar.inventorytrackingv2.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bazaar.inventorytrackingv2.repository.*;
import com.bazaar.inventorytrackingv2.dto.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;


    @Autowired
    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            StoreRepository storeRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryDTO getInventoryByProductAndStore(Long productId, Long storeId) {
        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        return convertToDTO(inventory);
    }


    @Transactional
    public InventoryDTO updateInventory(Long productId, Long storeId, InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        //  Prevents race conditions using Optimistic Locking
        if (inventoryDTO.getVersion() != null && !inventory.getVersion().equals(inventoryDTO.getVersion())) {
            throw new OptimisticLockingFailureException("Inventory data has been modified by another transaction.");
        }

        inventory.setQuantity(inventoryDTO.getQuantity());
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return convertToDTO(updatedInventory);
    }


    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryByStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        return inventoryRepository.findByStore(store).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
  }
        @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setProductId(inventory.getProduct().getId());
        dto.setStoreId(inventory.getStore().getId());
        dto.setQuantity(inventory.getQuantity());
        dto.setMinStockLevel(inventory.getMinStockLevel());
        dto.setMaxStockLevel(inventory.getMaxStockLevel());
        dto.setLastUpdated(inventory.getLastUpdated());
        return dto;
    }
}
