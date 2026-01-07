package com.bazaar.inventorytrackingv2.repository;
import com.bazaar.inventorytrackingv2.model.Inventory;
import com.bazaar.inventorytrackingv2.model.InventoryId;
import com.bazaar.inventorytrackingv2.model.Product;
import com.bazaar.inventorytrackingv2.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {
    List<Inventory> findByStore(Store store);
    List<Inventory> findByProduct(Product product);
    Optional<Inventory> findByProductAndStore(Product product, Store store);
    Optional<Inventory> findByProductIdAndStoreId(Long productId, Long storeId);

    @Query("SELECT i FROM Inventory i WHERE i.quantity < i.minStockLevel")
    List<Inventory> findLowStockItems();
}
