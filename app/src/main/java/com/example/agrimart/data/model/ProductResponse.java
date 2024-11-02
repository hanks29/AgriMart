package com.example.agrimart.data.model;

public class ProductResponse {
    private String name;
    private double price;

    private String imageUrls;

    public ProductResponse(String name, double price, String imageUrls) {
        this.name = name;
        this.price = price;
        this.imageUrls = imageUrls;
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
}
