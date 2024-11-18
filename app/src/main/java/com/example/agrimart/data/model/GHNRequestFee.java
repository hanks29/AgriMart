package com.example.agrimart.data.model;

import com.example.agrimart.data.model.ghn.Item;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GHNRequestFee implements Serializable {
    @SerializedName("service_type_id")
    private int serviceTypeId;

    @SerializedName("from_ward_code")
    private String fromWardCode;

    @SerializedName("from_district_id")
    private int fromDistrictId;

    @SerializedName("to_ward_code")
    private String toWardCode;

    @SerializedName("to_district_id")
    private int toDistrictId;

    @SerializedName("weight")
    private int weight;

    private List<Item> items;

    public GHNRequestFee() {
    }

    @SerializedName("service_type_id")
    public int getServiceTypeId() {
        return serviceTypeId;
    }

    @SerializedName("service_type_id")
    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    @SerializedName("from_ward_code")
    public String getFromWardCode() {
        return fromWardCode;
    }

    @SerializedName("from_ward_code")
    public void setFromWardCode(String fromWardCode) {
        this.fromWardCode = fromWardCode;
    }

    @SerializedName("from_district_id")
    public int getFromDistrictId() {
        return fromDistrictId;
    }

    @SerializedName("from_district_id")
    public void setFromDistrictId(int fromDistrictId) {
        this.fromDistrictId = fromDistrictId;
    }

    public String getToWardCode() {
        return toWardCode;
    }

    public void setToWardCode(String toWardCode) {
        this.toWardCode = toWardCode;
    }

    @SerializedName("to_district_id")
    public int getToDistrictId() {
        return toDistrictId;
    }

    @SerializedName("to_district_id")
    public void setToDistrictId(int toDistrictId) {
        this.toDistrictId = toDistrictId;
    }

    @SerializedName("weight")
    public int getWeight() {
        return weight;
    }

    @SerializedName("weight")
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
