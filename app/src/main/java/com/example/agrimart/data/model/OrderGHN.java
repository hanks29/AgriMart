package com.example.agrimart.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class OrderGHN implements Serializable {
    @JsonProperty("order_codes")
    private List<String> orderCodes;

    public OrderGHN() {
    }
    public OrderGHN(List<String> orderCodes) {
        this.orderCodes = orderCodes;
    }

    @JsonProperty("order_codes")
    public List<String> getOrderCodes() {
        return orderCodes;
    }

    @JsonProperty("order_codes")
    public void setOrderCodes(List<String> orderCodes) {
        this.orderCodes = orderCodes;
    }
}
