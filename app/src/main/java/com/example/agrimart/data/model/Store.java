package com.example.agrimart.data.model;

import java.util.List;

public class Store {
    private String id;
    private String store_name;
    private String store_avatar;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String phoneNumber;
    private String userId;
    private List<Product> products;

    public Store() {
    }

    public Store(String id, String name, String avatarUrl, String street, String ward, String district, String city, String phoneNumber) {
        this.id = id;
        this.store_name = name;
        this.store_avatar = avatarUrl;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return store_name;
    }

    public void setName(String name) {
        this.store_name = name;
    }

    public String getAvatarUrl() {
        return store_avatar;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.store_avatar = avatarUrl;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getFullAddress() {
        return street + ", " + ward + ", " + district + ", " + city;
    }
}