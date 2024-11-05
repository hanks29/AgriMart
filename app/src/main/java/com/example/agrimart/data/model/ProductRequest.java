package com.example.agrimart.data.model;

import com.google.firebase.encoders.annotations.Encodable;

import java.io.Serializable;
import java.util.List;

public class ProductRequest implements Serializable {
    private String name;
    private double price;


    private String category;
    private String description;
    private List<String> images;

    private int quantity;
    private String storeId;

    private String status;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return images;
    }

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
}
