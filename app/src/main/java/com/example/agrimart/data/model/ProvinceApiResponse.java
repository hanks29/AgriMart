package com.example.agrimart.data.model;

public class ProvinceApiResponse<T> {
    private int exitcode;
    private ProvinceData data; // ProvinceData chứa thông tin các tỉnh
    private String message;

    public int getExitcode() {
        return exitcode;
    }

    public ProvinceData getData() {
        return data;
    }
}
