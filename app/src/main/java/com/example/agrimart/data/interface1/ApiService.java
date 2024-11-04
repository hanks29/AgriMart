package com.example.agrimart.data.interface1;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
import com.example.agrimart.data.model.Province;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.Commune;

public interface ApiService {
    @GET("province")
    Call<List<Province>> getAllProvinces();

    @GET("district")
    Call<List<District>> getAllDistricts();

    @GET("commune")
    Call<List<Commune>> getAllCommune();
}
