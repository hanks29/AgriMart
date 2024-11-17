package com.example.agrimart.data.model;

import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    @SerializedName("userId")
    private String userId;
    @SerializedName("role")
    private String role;

    @PropertyName("fullName")
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;
    @SerializedName("sex")
    private String sex;
    @SerializedName("birthDate")
    private String birthDate;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    @PropertyName("addresses")
    @SerializedName("addresses")
    private List<Address> addresses;

    @PropertyName("store_phone_number")
    @SerializedName("store_phone_number")
    private String phoneNumberStore;

    @PropertyName("store_name")
    @SerializedName("store_name")
    private String storeName;

    @PropertyName("store_address")
    @SerializedName("store_address")
    private Address storeAddress;

    @PropertyName("store_avatar")
    @SerializedName("store_avatar")
    private String storeImage;

    @SerializedName("storeRating")
    private double storeRating;

    @PropertyName("paymentAccount")
    @SerializedName("paymentAccount")
    private PaymentAccount paymentAccount;

    @PropertyName("createdAt")
    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("updatedAt")
    private Date updatedAt;

    @PropertyName("userImage")
    @SerializedName("userImage")
    private String userImage;

    public User() {

    }

    public User(String userId, String role, String fullName, String email, String phoneNumber, String password, List<Address> addresses, String storeName, Address storeAddress, String storeImage, double storeRating, PaymentAccount paymentAccount, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.addresses = addresses;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeImage = storeImage;
        this.storeRating = storeRating;
        this.paymentAccount = paymentAccount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters...

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @PropertyName("fullName")
    public String getFullName() {
        return fullName;
    }

    @PropertyName("fullName")
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

    @PropertyName("addresses")
    public List<Address> getAddresses() {
        return addresses;
    }

    @PropertyName("addresses")
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @PropertyName("store_name")
    public String getStoreName() {
        return storeName;
    }

    @PropertyName("store_name")
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @PropertyName("store_address")
    @SerializedName("store_address")
    public Address getStoreAddress() {
        return storeAddress;
    }

    @PropertyName("store_address")
    @SerializedName("store_address")
    public void setStoreAddress(Address storeAddress) {
        this.storeAddress = storeAddress;
    }

    @PropertyName("store_avatar")
    @SerializedName("store_avatar")
    public String getStoreImage() {
        return storeImage;
    }

    @PropertyName("store_avatar")
    @SerializedName("store_avatar")
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @PropertyName("userImage")
    public String getUserImage() {
        return userImage;
    }

    @PropertyName("userImage")
    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    @PropertyName("store_phone_number")
    public String getPhoneNumberStore() {
        return phoneNumberStore;
    }

    @PropertyName("store_phone_number")
    public void setPhoneNumberStore(String phoneNumberStore) {
        this.phoneNumberStore = phoneNumberStore;
    }

    public static class Address implements Serializable {
        @PropertyName("street")
        @SerializedName("street")
        private String street;

        @PropertyName("district")
        @SerializedName("district")
        private String district;

        @PropertyName("ward")
        @SerializedName("ward")
        private String ward;

        @PropertyName("city")
        @SerializedName("city")
        private String city;

        public Address() {
        }

        public Address(String street, String district, String city) {
            this.street = street;
            this.district = district;
            this.city = city;
        }

        // Getters and setters...

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        @PropertyName("district")
        public String getDistrict() {
            return district;
        }

        @PropertyName("district")
        public void setDistrict(String district) {
            this.district = district;
        }

        @PropertyName("city")
        public String getCity() {
            return city;
        }

        @PropertyName("city")
        public void setCity(String city) {
            this.city = city;
        }

        @PropertyName("ward")
        public String getWard() {
            return ward;
        }

        @PropertyName("ward")
        public void setWard(String ward) {
            this.ward = ward;
        }
    }

    public static class PaymentAccount {
        @SerializedName("accountNumber")
        private String accountNumber;
        @SerializedName("bankName")
        private String bankName;
        @SerializedName("branchName")
        private String branchName;

        public PaymentAccount() {
        }

        public PaymentAccount(String accountNumber, String bankName, String branchName) {
            this.accountNumber = accountNumber;
            this.bankName = bankName;
            this.branchName = branchName;
        }

        // Getters and setters...
    }
}