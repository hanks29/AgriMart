package com.example.agrimart.data.model;

public class ProductCart {
    private String name;
    private double price;
    private String productId;

    public ProductCart() {
        // Bắt buộc phải có constructor rỗng khi lấy dữ liệu từ Firebase
    }

    public ProductCart(String name, int price, String productId) {
        this.name = name;
        this.price = price;
        this.productId = productId;
    }

    // Getter và Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
}