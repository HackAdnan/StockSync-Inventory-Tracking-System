package com.bazaar.inventorytrackingv2.model;
import java.io.Serializable;
import java.util.Objects;

public class InventoryId implements Serializable {
    private Long product;
    private Long store;

    public InventoryId() {}

    public InventoryId(Long product, Long store) {
        this.product = product;
        this.store = store;
    }

    // Getters and Setters
    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public Long getStore() {
        return store;
    }

    public void setStore(Long store) {
        this.store = store;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryId that = (InventoryId) o;
        return Objects.equals(product, that.product) &&
                Objects.equals(store, that.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, store);
    }
}

