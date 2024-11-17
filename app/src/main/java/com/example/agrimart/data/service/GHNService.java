package com.example.agrimart.data.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.agrimart.data.API.ApiGHN;
import com.example.agrimart.data.API.ConfigGHN;
import com.example.agrimart.data.interface1.GhnApiService;
import com.example.agrimart.data.model.ghn.GHNRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.Objects;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class GHNService {
    private final GhnApiService ghnApiService;
    private final String token;

    private final String shopId;
    public GHNService() {
        this.ghnApiService = ApiGHN.getClient().create(GhnApiService.class);
        this.token= ConfigGHN.TOKEN_SHOP_GHN;
        shopId=ConfigGHN.SHOP_ID;
    }

    public void getProvinceId(String provinceName, Callback<Integer> callback) {
        ghnApiService.getProvinces(token).enqueue(new retrofit2.Callback<JsonNode>() {
            @Override
            public void onResponse(@NonNull Call<JsonNode> call, @NonNull Response<JsonNode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer provinceId = extractProvinceId(response.body(), provinceName);
                    callback.onResponse(provinceId);
                } else {
                    Log.e("ADDRESS_USER", "Failed to retrieve province: " + response.code() + " " + response.message());
                    callback.onFailure(new Exception("Failed to retrieve province"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonNode> call, @NonNull Throwable t) {
                callback.onFailure(new Exception("API call failed"+t.getMessage()));
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
                    if (districtId != -1) {
                        callback.onResponse(districtId);
                    } else {
                        callback.onFailure(new Exception("District not found"));
                    }
                } else {
                    callback.onFailure(new Exception("Failed to retrieve district data"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonNode> call, @NonNull Throwable t) {
                callback.onFailure(new Exception("API call failed"+t.getMessage()));
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

    public void getWardCode(String wardName, int districtId, Callback<String> callback) {
        ghnApiService.getWards(token, districtId).enqueue(new retrofit2.Callback<JsonNode>() {
            @Override
            public void onResponse(@NonNull Call<JsonNode> call, @NonNull Response<JsonNode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String wardCode = extractWardCode(response.body(), wardName);
                    if (!Objects.equals(wardCode, "")) {
                        callback.onResponse(wardCode);
                    } else {
                        callback.onFailure(new Exception("Ward not found"));
                    }
                } else {
                    callback.onFailure(new Exception("Failed to retrieve ward data"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonNode> call, @NonNull Throwable t) {
                callback.onFailure(new Exception("API call failed"+t.getMessage()));
            }
        });
    }

    private String extractWardCode(JsonNode response, String wardName) {
        JsonNode data = response.path("data");
        for (JsonNode ward : data) {
            for (JsonNode name : ward.path("NameExtension")) {
                if (name.asText().equalsIgnoreCase(wardName)) {
                    return ward.path("WardCode").asText();
                }
            }
        }
        return "";
    }

    public void createShippingOrder(GHNRequest requestBody, Callback<JsonNode> callback) {
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        ghnApiService.createShippingOrder(token, shopId, "application/json", body).enqueue(new retrofit2.Callback<JsonNode>() {
            @Override
            public void onResponse(@NonNull Call<JsonNode> call, @NonNull Response<JsonNode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure(new Exception("Failed to create shipping order"+ response.code()+response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonNode> call, @NonNull Throwable t) {
                callback.onFailure(new Exception("API call failed"+t.getMessage()));
            }
        });
    }



    public interface Callback<T> {
        void onResponse(T result);
        void onFailure(Exception e);
    }
}
