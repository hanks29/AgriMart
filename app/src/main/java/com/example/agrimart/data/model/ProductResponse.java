package com.example.agrimart.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class ProductResponse implements Serializable {
    private String name;
    private double price;

    @PropertyName("category_id")
    private String category;
    private String description;
    private int quantity;

    private String productId;
    private String imageUrls;

    public ProductResponse(String name, double price, String imageUrls) {
        this.name = name;
        this.price = price;
        this.imageUrls = imageUrls;
    }
    public ProductResponse() {
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

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ProductResponse(String name, double price, String category, String description, int quantity, String imageUrls, String productId) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.imageUrls = imageUrls;
        this.productId = productId;
    }
}
