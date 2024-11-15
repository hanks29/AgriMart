package com.example.agrimart.adapter;


import com.example.agrimart.data.model.Rating;

import java.util.List;

public class UserRating {
    private List<Rating> ratings;
    private String userId;

    // Constructor mặc định (bắt buộc)
    public UserRating() {}

    // Constructor đầy đủ
    public UserRating(List<Rating> ratings, String userId) {
        this.ratings = ratings;
        this.userId = userId;
    }

    // Getter và Setter
    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
