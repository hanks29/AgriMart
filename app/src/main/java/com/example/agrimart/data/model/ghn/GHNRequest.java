package com.example.agrimart.data.model.ghn;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GHNRequest implements Serializable {
    @SerializedName("to_name")
    private String toName;

    @SerializedName("from_name")
    private String fromName;

    @SerializedName("from_phone")
    private String fromPhone;

    @SerializedName("from_address")
    private String fromAddress;

    @SerializedName("from_ward_name")
    private String fromWardName;

    @SerializedName("from_district_name")
    private String fromDistrictName;

    @SerializedName("from_province_name")
    private String fromProvinceName;

    @SerializedName("to_phone")
    private String toPhone;

    @SerializedName("to_address")
    private String toAddress;

    @SerializedName("to_ward_code")
    private String toWardCode;

    @SerializedName("to_district_id")
    private int toDistrictId;

    @SerializedName("weight")
    private int weight;

    @SerializedName("length")
    private int length;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    @SerializedName("service_type_id")
    private int serviceTypeId;

    @SerializedName("payment_type_id")
    private int paymentTypeId;

    @SerializedName("required_note")
    private String requiredNote;

    @SerializedName("cod_amount")
    private int codAmount;

    private List<Item> items;

    public GHNRequest() {
    }

    public GHNRequest(String toName, String fromName, String fromPhone, String fromAddress, String fromWardName, String fromDistrictName, String fromProvinceName, String toPhone, String toAddress, String toWardCode, int toDistrictId, int weight, int length, int width, int height, int serviceTypeId, int paymentTypeId, String requiredNote, int codAmount) {
        this.toName = toName;
        this.fromName = fromName;
        this.fromPhone = fromPhone;
        this.fromAddress = fromAddress;
        this.fromWardName = fromWardName;
        this.fromDistrictName = fromDistrictName;
        this.fromProvinceName = fromProvinceName;
        this.toPhone = toPhone;
        this.toAddress = toAddress;
        this.toWardCode = toWardCode;
        this.toDistrictId = toDistrictId;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.serviceTypeId = serviceTypeId;
        this.paymentTypeId = paymentTypeId;
        this.requiredNote = requiredNote;
        this.codAmount = codAmount;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromWardName() {
        return fromWardName;
    }

    public void setFromWardName(String fromWardName) {
        this.fromWardName = fromWardName;
    }

    public String getFromDistrictName() {
        return fromDistrictName;
    }

    public void setFromDistrictName(String fromDistrictName) {
        this.fromDistrictName = fromDistrictName;
    }

    public String getFromProvinceName() {
        return fromProvinceName;
    }

    public void setFromProvinceName(String fromProvinceName) {
        this.fromProvinceName = fromProvinceName;
    }

    public String getToPhone() {
        return toPhone;
    }

    public void setToPhone(String toPhone) {
        this.toPhone = toPhone;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getToWardCode() {
        return toWardCode;
    }

    public void setToWardCode(String toWardCode) {
        this.toWardCode = toWardCode;
    }

    public int getToDistrictId() {
        return toDistrictId;
    }

    public void setToDistrictId(int toDistrictId) {
        this.toDistrictId = toDistrictId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getRequiredNote() {
        return requiredNote;
    }

    public void setRequiredNote(String requiredNote) {
        this.requiredNote = requiredNote;
    }

    public int getCodAmount() {
        return codAmount;
    }

    public void setCodAmount(int codAmount) {
        this.codAmount = codAmount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
