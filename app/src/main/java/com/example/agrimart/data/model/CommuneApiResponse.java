package com.example.agrimart.data.model;

public class CommuneApiResponse<T>{
    private int exitcode;
    private CommuneData data; // ProvinceData chứa thông tin các tỉnh
    private String message;

    public int getExitcode() {
        return exitcode;
    }

    public CommuneData getData() {
        return data;
    }
}
