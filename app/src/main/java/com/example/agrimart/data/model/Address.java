package com.example.agrimart.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Address implements Parcelable {
    @SerializedName("default")
    private boolean isDefault;
    private String province;
    private String district;
    private String commune;
    @SerializedName("detailedAddressID")
    private String detailedAddressID;
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("street")
    private String street;
    @SerializedName("AddressId")
    private String AddressId;

    public Address() {}

    public Address(String AddressId, String name, String phone, String street, String commune, String district, String province, String detailedAddressID, boolean isDefault) {
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.detailedAddressID = detailedAddressID;
        this.isDefault = isDefault;
        this.AddressId = AddressId;
        this.commune = commune;
        this.district = district;
        this.province = province;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getDetailedAddressID() {
        return detailedAddressID;
    }

    public void setDetailedAddressID(String detailedAddressID) {
        this.detailedAddressID = detailedAddressID;
    }

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

    public String getAddressId() {
        return AddressId;
    }

    public void setAddressId(String addressId) {
        AddressId = addressId;
    }

    protected Address(Parcel in) {
        isDefault = in.readByte() != 0;
        province = in.readString();
        district = in.readString();
        commune = in.readString();
        detailedAddressID = in.readString();
        name = in.readString();
        phone = in.readString();
        street = in.readString();
        AddressId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeString(province);
        dest.writeString(district);
        dest.writeString(commune);
        dest.writeString(detailedAddressID);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(street);
        dest.writeString(AddressId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}