package com.example.agrimart.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    @SerializedName("userId")
    private String userId;
    @SerializedName("role")
    private String role;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("password")
    private String password;
    @SerializedName("address")
    private Address address;
    @SerializedName("storeName")
    private String storeName;
    @SerializedName("storeAddress")
    private Address storeAddress;
    @SerializedName("storeImage")
    private String storeImage;
    @SerializedName("storeRating")
    private double storeRating;
    @SerializedName("paymentAccount")
    private PaymentAccount paymentAccount;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updatedAt")
    private Date updatedAt;

    public User(String userId, String role, String fullName, String email, String phoneNumber, String password, Address address, String storeName, Address storeAddress, String storeImage, double storeRating, PaymentAccount paymentAccount, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.address = address;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeImage = storeImage;
        this.storeRating = storeRating;
        this.paymentAccount = paymentAccount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Address getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(Address storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public double getStoreRating() {
        return storeRating;
    }

    public void setStoreRating(double storeRating) {
        this.storeRating = storeRating;
    }

    public PaymentAccount getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(PaymentAccount paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Address {
        @SerializedName("street")
        private String street;
        @SerializedName("district")
        private String district;
        @SerializedName("city")
        private String city;

        // Constructors, getters, and setters...
    }

    public static class PaymentAccount {
        @SerializedName("accountNumber")
        private String accountNumber;
        @SerializedName("bankName")
        private String bankName;
        @SerializedName("branchName")
        private String branchName;

        // Constructors, getters, and setters...
    }

    // Getters and setters for User class fields
    // ...
}