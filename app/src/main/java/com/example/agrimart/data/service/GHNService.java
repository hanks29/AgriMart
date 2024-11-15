package com.example.agrimart.data.service;

import androidx.annotation.NonNull;

import com.example.agrimart.data.API.ApiGHN;
import com.example.agrimart.data.API.ConfigGHN;
import com.example.agrimart.data.interface1.GhnApiService;
import com.fasterxml.jackson.databind.JsonNode;

import retrofit2.Call;
import retrofit2.Response;

public class GHNService {
    private final GhnApiService ghnApiService;
    private final String token;

    public GHNService() {
        this.ghnApiService = ApiGHN.getClient().create(GhnApiService.class);
        this.token= ConfigGHN.TOKEN_SHOP_GHN;
    }

    public void getProvinceId(String provinceName, Callback<Integer> callback) {
        ghnApiService.getProvinces(token).enqueue(new retrofit2.Callback<JsonNode>() {
            @Override
            public void onResponse(@NonNull Call<JsonNode> call, @NonNull Response<JsonNode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer provinceId = extractProvinceId(response.body(), provinceName);
                    callback.onResponse(provinceId);
                } else {
                    callback.onFailure(new Exception("Failed to retrieve province"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonNode> call, @NonNull Throwable t) {
                callback.onFailure(new Exception("API call failed"));
            }
        });
    }
    private Integer extractProvinceId(JsonNode response, String provinceName) {
        JsonNode data = response.path("data");
        for (JsonNode province : data) {
            for (JsonNode name : province.path("NameExtension")) {
                if (name.asText().equalsIgnoreCase(provinceName)) {
                    return province.path("ProvinceID").asInt();
                }
            }
        }
        return -1;
    }

    public void getDistrictId(String districtName, int provinceId, Callback<Integer> callback) {
        ghnApiService.getDistricts(token, provinceId).enqueue(new retrofit2.Callback<JsonNode>() {
            @Override
            public void onResponse(@NonNull Call<JsonNode> call, @NonNull Response<JsonNode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer districtId = extractDistrictId(response.body(), districtName);
                    callback.onResponse(districtId);
                } else {
                    callback.onFailure(new Exception("Failed to retrieve district"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonNode> call, @NonNull Throwable t) {
                callback.onFailure(new Exception("API call failed"));
            }
        });
    }

    private Integer extractDistrictId(JsonNode response, String districtName) {
        JsonNode data = response.path("data");
        for (JsonNode district : data) {
            for (JsonNode name : district.path("NameExtension")) {
                if (name.asText().equalsIgnoreCase(districtName)) {
                    return district.path("DistrictID").asInt();
                }
            }
        }
        return -1;
    }

    public void getWardCode(String wardName, int districtId, Callback<Integer> callback) {
        ghnApiService.getWards(token, districtId).enqueue(new retrofit2.Callback<JsonNode>() {
            @Override
            public void onResponse(@NonNull Call<JsonNode> call, @NonNull Response<JsonNode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer wardCode = extractWardCode(response.body(), wardName);
                    callback.onResponse(wardCode);
                } else {
                    callback.onFailure(new Exception("Failed to retrieve ward"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonNode> call, @NonNull Throwable t) {
                callback.onFailure(new Exception("API call failed"));
            }
        });
    }

    private Integer extractWardCode(JsonNode response, String wardName) {
        JsonNode data = response.path("data");
        for (JsonNode ward : data) {
            for (JsonNode name : ward.path("NameExtension")) {
                if (name.asText().equalsIgnoreCase(wardName)) {
                    return ward.path("WardCode").asInt();
                }
            }
        }
        return -1;
    }


    public interface Callback<T> {
        void onResponse(T result);
        void onFailure(Exception e);
    }
}
