package com.example.agrimart.data.model;

public class Address {
    private String name;
    private String phone;
    private String street;
    private String detailedAddress;
    private boolean isDefault;

    // Constructor
    public Address(String name, String phone, String street, String detailedAddress, boolean isDefault) {
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.detailedAddress = detailedAddress;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
