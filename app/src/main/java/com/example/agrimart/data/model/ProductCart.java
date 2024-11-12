package com.example.agrimart.data.model;

public class ProductCart {
    private String product_id;
    private int quantity;

    public ProductCart() {
        // Bắt buộc phải có constructor rỗng khi lấy dữ liệu từ Firebase
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}