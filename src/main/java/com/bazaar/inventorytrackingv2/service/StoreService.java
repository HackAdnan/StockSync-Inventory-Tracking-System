package com.bazaar.inventorytrackingv2.service;
import com.bazaar.inventorytrackingv2.dto.StoreDTO;
import com.bazaar.inventorytrackingv2.exception.ResourceNotFoundException;
import com.bazaar.inventorytrackingv2.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bazaar.inventorytrackingv2.repository.StoreRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public List<StoreDTO> getAllStores() {
        return storeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StoreDTO getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        return convertToDTO(store);
    }

    @Transactional(readOnly = true)
    public StoreDTO getStoreByCode(String code) {
        Store store = storeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with code: " + code));
        return convertToDTO(store);
    }

    @Transactional
    public StoreDTO createStore(StoreDTO storeDTO) {
        Store store = new Store();
        store.setCode(storeDTO.getCode());
        store.setName(storeDTO.getName());
        store.setAddress(storeDTO.getAddress());
        store.setContactPhone(storeDTO.getContactPhone());
        store.setContactEmail(storeDTO.getContactEmail());
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());

        Store savedStore = storeRepository.save(store);
        return convertToDTO(savedStore);
    }

    @Transactional
    public StoreDTO updateStore(Long id, StoreDTO storeDTO) {
        Store existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        existingStore.setName(storeDTO.getName());
        existingStore.setAddress(storeDTO.getAddress());
        existingStore.setContactPhone(storeDTO.getContactPhone());
        existingStore.setContactEmail(storeDTO.getContactEmail());
        existingStore.setUpdatedAt(LocalDateTime.now());

        Store updatedStore = storeRepository.save(existingStore);
        return convertToDTO(updatedStore);
    }



    @Transactional
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store not found with id: " + id);
        }
        storeRepository.deleteById(id);
    }

    private StoreDTO convertToDTO(Store store) {
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setCode(store.getCode());
        dto.setName(store.getName());
        dto.setAddress(store.getAddress());
        dto.setContactPhone(store.getContactPhone());
        dto.setContactEmail(store.getContactEmail());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        return dto;
    }
}