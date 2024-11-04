package com.example.agrimart.data.model;

public class WardApiResponse<T>{
    private int exitcode;
    private WardData data; // ProvinceData chứa thông tin các tỉnh
    private String message;

    public int getExitcode() {
        return exitcode;
    }

    public WardData getData() {
        return data;
    }
}
