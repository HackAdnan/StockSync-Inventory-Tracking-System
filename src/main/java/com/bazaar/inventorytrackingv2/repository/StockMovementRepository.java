package com.bazaar.inventorytrackingv2.repository;
import com.bazaar.inventorytrackingv2.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByStore(Store store);
    List<StockMovement> findByProduct(Product product);
    List<StockMovement> findByStoreAndProduct(Store store, Product product);
    List<StockMovement> findByType(MovementType type);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.timestamp BETWEEN :startDate AND :endDate")
    List<StockMovement> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.store = :store AND sm.timestamp BETWEEN :startDate AND :endDate")
    List<StockMovement> findByStoreAndDateRange(
            @Param("store") Store store,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

