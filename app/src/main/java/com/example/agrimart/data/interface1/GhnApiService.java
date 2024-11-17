package com.example.agrimart.data.interface1;

import com.example.agrimart.data.model.ghn.GHNRequest;
import com.fasterxml.jackson.databind.JsonNode;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GhnApiService {

    @GET("master-data/province")
    Call<JsonNode> getProvinces(@Header("token") String token);

    @GET("master-data/district")
    Call<JsonNode> getDistricts(@Header("token") String token, @Query("province_id") int provinceId);

    @GET("master-data/ward")
    Call<JsonNode> getWards(@Header("token") String token, @Query("district_id") int districtId);

    @POST("v2/shipping-order/create")
    Call<JsonNode> createShippingOrder(
            @Header("token") String token,
            @Header("ShopId") String shopId,
            @Header("Content-Type") String contentType,
            @Body RequestBody requestBody
    );

}
