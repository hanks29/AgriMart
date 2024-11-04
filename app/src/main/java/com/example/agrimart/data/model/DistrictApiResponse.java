package com.example.agrimart.data.model;

import java.util.List;

public class DistrictApiResponse <T>{
    private int exitcode;
    private DistrictData data; // ProvinceData chứa thông tin các tỉnh
    private String message;

    public int getExitcode() {
        return exitcode;
    }

    public DistrictData getData() {
        return data;
    }
}
