package com.example.agrimart.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Cart implements Parcelable {
    private List<Product> products;
    private String storeId;
    private String store_name;
    private String updatedAt;
    private String userId;
    private List<ProductCart> productCart;
    private boolean checked;
    private double totalPrice;

    public Cart() {
        // Constructor mặc định
    }

    // Constructor từ Parcel
    protected Cart(Parcel in) {
        storeId = in.readString();
        store_name = in.readString();
        updatedAt = in.readString();
        userId = in.readString();
        checked = in.readByte() != 0;
        totalPrice = in.readDouble();

        // Đọc danh sách products từ Parcel
        products = new ArrayList<>();
        in.readList(products, Product.class.getClassLoader());

        // Đọc danh sách productCart từ Parcel
        productCart = new ArrayList<>();
        in.readList(productCart, ProductCart.class.getClassLoader());
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    // Hàm thêm sản phẩm vào StoreCart
    public void addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
    }

    public void setProduct(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public List<ProductCart> getProductCart() {
        return productCart;
    }

    public void setProductCart(List<ProductCart> productCart) {
        this.productCart = productCart;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public double getTotalPrice() {
        double total = 0;
        for (Product product : products) {
            if (product.isChecked()) {
                total += product.getPrice() * product.getQuantity();
            }
        }
        return total;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storeId);
        dest.writeString(store_name);
        dest.writeString(updatedAt);
        dest.writeString(userId);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeDouble(totalPrice);

        // Ghi danh sách products vào Parcel
        dest.writeList(products);

        // Ghi danh sách productCart vào Parcel
        dest.writeList(productCart);
    }
}
