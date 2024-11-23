package com.example.agrimart.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductReview {
    @JsonProperty("product_id")
    private String productId;
    private String imageResId;
    private String productName;
    private int quantity;
    private float rating;
    private String status;
    private String review;

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageResId() {
        return imageResId;
    }

    public void setImageResId(String imageResId) {
        this.imageResId = imageResId;
    }

    public ProductReview(){

    }


    public ProductReview(String imageResId, String productName, int quantity, float rating, String status, String review) {
        this.imageResId = imageResId;
        this.productName = productName;
        this.quantity = quantity;
        this.rating = rating;
        this.status = status;
        this.review = review;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

}
