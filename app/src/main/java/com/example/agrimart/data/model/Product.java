package com.example.agrimart.data.model;

public class Product {
    private int image;
    private String name;
    private String price;
    private String unit;
    private String active;

    public Product() {
    }

    public Product(int image, String name, String price, String unit) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.unit = unit;
    }

    public Product(int image, String name, String price, String unit, String active) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.active = active;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
