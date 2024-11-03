package com.example.agrimart.data.interface1;

import com.example.agrimart.data.model.DistrictApiResponse;
import com.example.agrimart.data.model.ProvinceApiResponse;
import com.example.agrimart.data.model.WardApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("provinces/getAll")
    Call<ProvinceApiResponse> getAllProvinces(@Query("limit") int limit);

    @GET("districts/getAll")
    Call<DistrictApiResponse> getAllDistricts(@Query("limit") int limit);

    @GET("wards/getAll")
    Call<WardApiResponse> getAllWards(@Query("limit") int limit);
}



