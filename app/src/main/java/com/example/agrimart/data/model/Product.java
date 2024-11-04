package com.example.agrimart.data.model;

public class Product {
    private String image;
    private String name;
    private double price;
    private String active;

    public Product() {
    }

    public Product(String image, String name, double price) {
        this.image = image;
        this.name = name;
        this.price = price;
    }

    public Product(String image, String name, double price, String active) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.active = active;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}