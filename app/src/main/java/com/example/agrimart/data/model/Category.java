package com.example.agrimart.data.model;

public class Category {
    private String img;
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

    public String getName() {
        return name;
    }

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