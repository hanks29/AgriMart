package com.example.agrimart.data.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Product> products;
    private String storeId;
    private String store_name;
    private String updatedAt;
    private String userId;
    private List<ProductCart> productCart;
    private boolean checked;

    // Hàm thêm sản phẩm vào StoreCart
    public void addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
    }



    public void setProduct(List<Product> product) {

        this.products = product;
    }

    public List<Product> getProducts() {
        if (products == null)
        {
            products = new ArrayList<>();
        }
        return products;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public List<ProductCart> getProductCart() {
        return productCart;
    }

    public void setProductCart(List<ProductCart> productCart) {
        this.productCart = productCart;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
