package com.example.agrimart.data.model;


import com.google.firebase.Timestamp;

public class Rating {
    private String userId;
    private String rating;
    private String review;
    private String userImage;
    private String fullName;
    private Timestamp updatedAt;
    private String status;
    private String quantity;

    // Constructor mặc định (bắt buộc)
    public Rating() {}

    // Constructor đầy đủ
    public Rating(String userId, String rating, String review, String updatedAt) {

    }

    public Rating(String userId, String rating, String review, Timestamp updatedAt, String userImage, String fullName) {
        this.userId = userId;
        this.rating = rating;
        this.review = review;
        this.userImage = userImage;
        this.fullName = fullName;
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



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
