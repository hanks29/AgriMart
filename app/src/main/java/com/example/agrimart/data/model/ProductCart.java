package com.example.agrimart.data.model;

public class ProductCart {
    private String product_id;
    private int quantity;
    private boolean checked;

    public ProductCart() {
        // Bắt buộc phải có constructor rỗng khi lấy dữ liệu từ Firebase
    }

    public ProductCart(String product_id, int quantity)
    {
        this.product_id = product_id;
        this.quantity = quantity;
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


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}