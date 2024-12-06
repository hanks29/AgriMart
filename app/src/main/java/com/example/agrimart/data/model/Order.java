package com.example.agrimart.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    @PropertyName("shipping_name")
    private String shippingName;

    @PropertyName("shipping_fee")
    private Double shippingFee;

    private String statusDelivery;

    private String storeName;

    private String paymentMethod;

    private boolean checkRating;

    private String username;

    private String phonenumber;

    @PropertyName("created_at")
    private Timestamp createdAt;

    private String transactionId;

    private Timestamp transactionDate;

    @PropertyName("vnp_TxnRef")
    private String vnpTxnRef;

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

    @PropertyName("products")
    public List<Product> getProducts() {
        return products;
    }

    @PropertyName("products")
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isCheckRating() {
        return checkRating;
    }

    public void setCheckRating(boolean checkRating) {
        this.checkRating = checkRating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @PropertyName("shipping_name")
    public String getShippingName() {
        return shippingName;
    }

    @PropertyName("shipping_name")
    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    @PropertyName("shipping_fee")
    public Double getShippingFee() {
        return shippingFee;
    }

    @PropertyName("shipping_fee")
    public void setShippingFee(Double shippingFee) {
        this.shippingFee = shippingFee;
    }

    private long createdAtMillis;  // Chúng ta sẽ sử dụng long thay vì Timestamp để truyền qua Intent
    // Getter và setter cho createdAtMillis
    public long getCreatedAtMillis() {
        return createdAtMillis;
    }

    @PropertyName("created_at")
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAtMillis = createdAt.toDate().getTime(); // Chuyển Timestamp thành long
    }

    @PropertyName("created_at")
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public boolean checkTime() {
        // Lấy thời gian hiện tại
        long currentTimeMillis = System.currentTimeMillis();

        // Tính khoảng cách thời gian giữa `createdAtMillis` và thời gian hiện tại
        long differenceInMillis = currentTimeMillis - createdAtMillis;

        // Kiểm tra nếu khoảng cách thời gian lớn hơn 6 tiếng (6 * 60 * 60 * 1000 milliseconds)
        return differenceInMillis <= 6 * 60 * 60 * 1000; // Nếu trên 6 tiếng

    }


    private long transactionDateMillis;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    private boolean refund;

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDateMillis = transactionDate.toDate().getTime();
    }

    @PropertyName("vnp_TxnRef")
    public String getVnpTxnRef() {
        return vnpTxnRef;
    }

    @PropertyName("vnp_TxnRef")
    public void setVnpTxnRef(String vnpTxnRef) {
        this.vnpTxnRef = vnpTxnRef;
    }

    public String getFormattedCreatedAtDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date(createdAtMillis);
        return dateFormat.format(date);
    }

    public long getTransactionDateMillis() {
        return transactionDateMillis;
    }

    public void setTransactionDateMillis(long transactionDateMillis) {
        this.transactionDateMillis = transactionDateMillis;
        this.createdAtMillis = createdAt.toDate().getTime();
    }

    public boolean isRefund() {
        return refund;
    }

    public void setRefund(boolean refund) {
        this.refund = refund;
    }
}