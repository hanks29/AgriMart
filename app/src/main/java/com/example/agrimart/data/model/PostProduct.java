// PostProduct.java
package com.example.agrimart.data.model;

public class PostProduct {
    private final String title;
    private final String price;
    private final int imageResId;

    public PostProduct(String title, String price, int imageResId) {
        this.title = title;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}
