package com.example.agrimart.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    @PropertyName("orderId")
    private String orderId;

    @PropertyName("product_id")
    private List<String> products;

    @PropertyName("status")
    private String status;

    @PropertyName("total_price")
    private int totalPrice;

    @PropertyName("userId")
    private String userId;

    public Order() {
    }

    public Order(String orderId, List<String> products, String status, int totalPrice) {
        this.orderId = orderId;
        this.products = products;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    @PropertyName("orderId")
    public String getOrderId() {
        return orderId;
    }

    @PropertyName("orderId")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @PropertyName("product_id")
    public List<String> getProducts() {
        return products;
    }

    @PropertyName("product_id")
    public void setProducts(List<String> products) {
        this.products = products;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("total_price")
    public int getTotalPrice() {
        return totalPrice;
    }

    @PropertyName("total_price")
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    @PropertyName("userId")
    public String getUserId() {
        return userId;
    }

    @PropertyName("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
