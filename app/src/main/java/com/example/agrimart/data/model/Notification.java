package com.example.agrimart.data.model;

public class Notification {
    private String title;
    private String message;
    private long timestamp;
    private String img;
    private String userId;

    public Notification(String title, String message, long timestamp, String imageUrl) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.img = imageUrl;
    }

    public Notification() {
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return img;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}