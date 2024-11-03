package com.example.agrimart.viewmodel;

import retrofit2.Call;
import com.example.agrimart.data.interface1.ApiService;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.DistrictApiResponse;
import com.example.agrimart.data.model.Province;
import com.example.agrimart.data.model.ProvinceApiResponse;
import com.example.agrimart.data.model.Ward;
import com.example.agrimart.data.model.WardApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetAPIAddressViewModel {
    private final ApiService apiService;

    private static final String BASE_URL = "https://vn-public-apis.fpo.vn/";

    public GetAPIAddressViewModel() {
        // Khởi tạo Retrofit và ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(ApiService.class);
    }

    public void loadProvinces(Consumer<List<Province>> onSuccess, Consumer<String> onError) {
        apiService.getAllProvinces(-1).enqueue(new Callback<ProvinceApiResponse>() {
            @Override
            public void onResponse(Call<ProvinceApiResponse> call, Response<ProvinceApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProvinceApiResponse apiResponse = response.body();
                    if (apiResponse.getExitcode() == 1) {
                        List<Province> provinces = apiResponse.getData().getData();
                        onSuccess.accept(provinces);
                    } else {
                        onError.accept("Lỗi khi tải các tỉnh.");
                    }
                } else {
                    onError.accept("Lỗi khi tải các tỉnh: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProvinceApiResponse> call, Throwable t) {
                onError.accept("Lỗi khi tải các tỉnh: " + t.getMessage());
            }
        });
    }

    public void loadDistrictsByProvince(String provinceCode, Consumer<List<District>> onSuccess, Consumer<String> onError) {
        apiService.getAllDistricts(-1).enqueue(new Callback<DistrictApiResponse>() {
            @Override
            public void onResponse(Call<DistrictApiResponse> call, Response<DistrictApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DistrictApiResponse apiResponse = response.body();
                    if (apiResponse.getExitcode() == 1) {
                        List<District> allDistricts = apiResponse.getData().getData();
                        List<District> filteredDistricts = new ArrayList<>();
                        for (District district : allDistricts) {
                            if (district.getParentCode().equals(provinceCode)) {
                                filteredDistricts.add(district);
                            }
                        }
                        onSuccess.accept(filteredDistricts);
                    } else {
                        onError.accept("Lỗi khi tải huyện.");
                    }
                } else {
                    onError.accept("Lỗi khi tải huyện: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DistrictApiResponse> call, Throwable t) {
                onError.accept("Lỗi khi tải huyện: " + t.getMessage());
            }
        });
    }

    public void loadWardsByDistrict(String districtCode, Consumer<List<Ward>> onSuccess, Consumer<String> onError) {
        apiService.getAllWards(-1).enqueue(new Callback<WardApiResponse>() {
            @Override
            public void onResponse(Call<WardApiResponse> call, Response<WardApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WardApiResponse apiResponse = response.body();
                    if (apiResponse.getExitcode() == 1) {
                        List<Ward> allWards = apiResponse.getData().getData();
                        List<Ward> filteredWards = new ArrayList<>();
                        for (Ward ward : allWards) {
                            if (ward.getParentCode().equals(districtCode)) {
                                filteredWards.add(ward);
                            }
                        }
                        onSuccess.accept(filteredWards);
                    } else {
                        onError.accept("Lỗi khi tải xã.");
                    }
                } else {
                    onError.accept("Lỗi khi tải xã: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WardApiResponse> call, Throwable t) {
                onError.accept("Lỗi khi tải xã: " + t.getMessage());
            }
        });
    }
}
