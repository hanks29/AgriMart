package com.example.agrimart.data.model;

import java.io.Serializable;

public class AddressRequestProduct implements Serializable {
    private String city;
    private String district;
    private String street;
    private String ward;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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

    public AddressRequestProduct() {
    }

    public AddressRequestProduct(String city, String district, String street, String ward) {
        this.city = city;
        this.district = district;
        this.street = street;
        this.ward = ward;
    }
}
