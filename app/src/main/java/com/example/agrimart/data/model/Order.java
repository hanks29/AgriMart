package com.example.agrimart.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    @PropertyName("orderId")
    private String orderId;

    @PropertyName("products")
    private List<Product> products;

    @PropertyName("status")
    private String status;

    @PropertyName("total_price")
    private int totalPrice;

    @PropertyName("userId")
    private String userId;

    @PropertyName("storeId")
    private String sellerId;

    @PropertyName("order_code")
    private String orderCode;

    @PropertyName("address")
    private String address;

    private String statusDelivery;

    public Order() {
    }

    public Order(String orderId, String status, int totalPrice) {
        this.orderId = orderId;
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

    @PropertyName("order_code")
    public String getOrderCode() {
        return orderCode;
    }

    @PropertyName("order_code")
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getStatusDelivery() {
        return statusDelivery;
    }

    public void setStatusDelivery(String statusDelivery) {
        this.statusDelivery = statusDelivery;
    }

    @PropertyName("address")
    public String getAddress() {
        return address;
    }

    @PropertyName("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @PropertyName("storeId")
    public String getSellerId() {
        return sellerId;
    }

    @PropertyName("storeId")
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
