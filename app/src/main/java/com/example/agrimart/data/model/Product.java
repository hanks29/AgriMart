package com.example.agrimart.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Product implements Serializable {

    private String product_id;
    private String name;
    private double price;
    @PropertyName("status")
    private String active;
    @PropertyName("category_id")
    private String category;
    private String description;
    private int height;
    private List<String> images;
    private int length;
    private int quantity;
    private String storeId;
    private int weight;
    private int width;
    private int soldQuantity;
    private String created_at;
    private boolean checked;


    public Product() {
    }

    public Product(String name, double price, List<String> images) {
        this.name = name;
        this.price = price;
        this.images = images;
    }

    public Product(String product_id, String name, double price, String active, String category, String description, int height, List<String> images, int length, int quantity, String storeId, int weight, int width, int soldQuantity) {
        this.product_id = product_id;
        this.name = name;
        this.price = price;
        this.active = active;
        this.category = category;
        this.description = description;
        this.height = height;
        this.images = images;
        this.length = length;
        this.quantity = quantity;
        this.storeId = storeId;
        this.weight = weight;
        this.width = width;
        this.soldQuantity = soldQuantity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public static class Address {
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
    }

    public Product(String imageUrl, String name, double price) {
        this.images = Collections.singletonList(imageUrl);
        this.name = name;
        this.price = price;
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

    @PropertyName("status")
    public String getActive() {
        return active;
    }

    @PropertyName("status")
    public void setActive(String active) {
        this.active = active;
    }

    @PropertyName("category_id")
    public String getCategory() {
        return category;
    }

    @PropertyName("category_id")
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
}