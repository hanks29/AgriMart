package com.example.agrimart.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class ProductReview {
    @PropertyName("product_id")
    private String productId;
    private String productName;
    private String quantity;
    private String productImage;
    private float rating;
    private String status;
    private String review;
    private Timestamp updatedAt;


    public ProductReview(String productName, String quantity, String productImage, float rating, String status, String review) {
        this.productName = productName;
        this.quantity = quantity;
        this.productImage = productImage;
        this.rating = rating;
        this.status = status;
        this.review = review;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getProductImage() {
        return productImage;
    }

    public float getRating() {
        return rating;
    }

    public String getStatus() {
        return status;
    }

    public String getReview() {
        return review;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
