// PostProduct.java
package com.example.agrimart.data.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class PostProduct implements Serializable {
    private String title;
    private String price;
    private int imageResId;
    private String category;

    public PostProduct() {

    }



    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }


    public PostProduct(String title, String price, int imageResId) {
        this.title = title;
        this.price = price;
        this.imageResId = imageResId;
    }
}
