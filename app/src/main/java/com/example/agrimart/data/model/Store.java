package com.example.agrimart.data.model;

public class Store {
    private String name;
    private String avatarUrl;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String phoneNumber;

    public Store() {
    }

    public Store(String name, String avatarUrl, String street, String ward, String district, String city, String phoneNumber) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFullAddress() {
        return street + ", " + ward + ", " + district + ", " + city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
