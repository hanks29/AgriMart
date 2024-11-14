package com.example.agrimart.data.model;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class Category implements Serializable {
    private String img;

    @PropertyName("name")
    private String name;
    private String id;

    public Category() {
    }

    public Category(String img, String name, String id) {
        this.img = img;
        this.name = name;
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}