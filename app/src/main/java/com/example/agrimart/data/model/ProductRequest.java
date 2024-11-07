package com.example.agrimart.data.model;

import com.google.firebase.encoders.annotations.Encodable;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class ProductRequest implements Serializable {
    private String name;
    private double price;

    @PropertyName("product_id")
    private String productId;

    @PropertyName("category_id")
    private String category;

    private String description;

    @PropertyName("images")
    private List<String> images;


    private int quantity;
    private String storeId;

    @PropertyName("created_at")
    private String createdAt;

    @PropertyName("updated_at")
    private String updatedAt;

    private String status;
    private  AddressRequestProduct address;

    public AddressRequestProduct getAddress() {
        return address;
    }

    public void setAddress(AddressRequestProduct address) {
        this.address = address;
    }

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @PropertyName("category_id")
    public String getCategory() {
        return category;
    }

    @PropertyName("category_id")
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("images")
    public List<String> getImageUrls() {
        return images;
    }

    @PropertyName("images")
    public void setImageUrls(List<String> imageUrls) {
        this.images = imageUrls;
    }



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @PropertyName("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @PropertyName("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @PropertyName("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PropertyName("product_id")
    public String getProductId() {
        return productId;
    }

    @PropertyName("product_id")
    public void setProductId(String productId) {
        this.productId = productId;
    }
}
