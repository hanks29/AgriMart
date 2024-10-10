package com.example.agrimart.data.model;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("default")
    private boolean isDefault;
    @SerializedName("detailedAddress")
    private String detailedAddress;
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("street")
    private String street;

    public String getAddressId() {
        return this.AddressId;
    }

    public void setAddressId(String AddressId) {
        this.AddressId = AddressId;
    }

    @SerializedName("AddressId")
    private String AddressId;
    public Address() {

    }
    // Constructor
    public Address(String AddressId,String name, String phone, String street, String detailedAddress, boolean isDefault) {
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.detailedAddress = detailedAddress;
        this.isDefault = isDefault;
        this.AddressId = AddressId;
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
