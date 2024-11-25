package com.example.agrimart.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable, Parcelable {
    private String product_id;
    private String name;
    private double price;
    private String active;
    private String unit;

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

    public Product() {}

    protected Product(Parcel in) {
        product_id = in.readString();
        name = in.readString();
        price = in.readDouble();
        active = in.readString();
        unit = in.readString();
        category = in.readString();
        description = in.readString();
        height = in.readInt();
        images = in.createStringArrayList();
        length = in.readInt();
        quantity = in.readInt();
        storeId = in.readString();
        weight = in.readInt();
        width = in.readInt();
        soldQuantity = in.readInt();
        created_at = in.readString();
        checked = in.readByte() != 0;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(active);
        dest.writeString(unit);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeInt(height);
        dest.writeStringList(images);
        dest.writeInt(length);
        dest.writeInt(quantity);
        dest.writeString(storeId);
        dest.writeInt(weight);
        dest.writeInt(width);
        dest.writeInt(soldQuantity);
        dest.writeString(created_at);
        dest.writeByte((byte) (checked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}