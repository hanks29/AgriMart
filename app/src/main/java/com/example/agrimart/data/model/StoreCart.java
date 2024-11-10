package com.example.agrimart.data.model;

import java.util.List;

public class StoreCart {
    private String storeId;
    private String updatedAt;
    private String userId;
    private List<ProductCart> products;

    public StoreCart() {
        // Constructor rỗng
    }

    public StoreCart(String storeId, String updatedAt, String userId, List<ProductCart> products) {
        this.storeId = storeId;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.products = products;
    }

    // Getter và Setter
    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<ProductCart> getProducts() { return products; }
    public void setProducts(List<ProductCart> products) { this.products = products; }
}
