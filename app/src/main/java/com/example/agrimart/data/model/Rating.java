package com.example.agrimart.data.model;

public class Rating {
    private String userId;
    private String rating;
    private String review;
    private String updatedAt;

    // Constructor mặc định (bắt buộc)
    public Rating() {}

    // Constructor đầy đủ
    public Rating(String userId, String rating, String review, String updatedAt) {
        this.userId = userId;
        this.rating = rating;
        this.review = review;
        this.updatedAt = updatedAt;
    }

    // Getter và Setter


    public String getRating() {
        return  rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
