package com.example.agrimart.viewmodel;

import retrofit2.Call;
import com.example.agrimart.data.interface1.ApiService;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.Province;
import com.example.agrimart.data.model.Commune;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetAPIAddressViewModel {
    private final ApiService apiService;

    private static final String BASE_URL = "https://vietnam-administrative-division-json-server-swart.vercel.app/";

    public GetAPIAddressViewModel() {
        // Khởi tạo Retrofit và ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(ApiService.class);
    }

    // Load danh sách tỉnh/thành phố
    public void loadProvinces(Consumer<List<Province>> onSuccess, Consumer<String> onError) {
        apiService.getAllProvinces().enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Trả về danh sách các tỉnh trực tiếp cho onSuccess
                    onSuccess.accept(response.body());
                } else {
                    // Xử lý lỗi khi phản hồi không thành công với mã lỗi
                    onError.accept("Lỗi khi tải các tỉnh: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                // Xử lý lỗi khi yêu cầu thất bại
                onError.accept("Lỗi khi tải các tỉnh: " + t.getMessage());
            }
        });
    }

    // Load danh sách huyện dựa trên mã tỉnh
    public void loadDistrictsByProvince(String provinceCode, Consumer<List<District>> onSuccess, Consumer<String> onError) {
        apiService.getAllDistricts().enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<District> allDistricts = response.body();
                    List<District> filteredDistricts = new ArrayList<>();
                    for (District district : allDistricts) {
                        if (district.getIdProvince().equals(provinceCode)) {
                            filteredDistricts.add(district);
                        }
                    }
                    onSuccess.accept(filteredDistricts);
                } else {
                    onError.accept("Lỗi khi tải huyện: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                onError.accept("Lỗi khi tải huyện: " + t.getMessage());
            }
        });
    }

    // Load danh sách xã/phường dựa trên mã huyện
    public void loadCommuneByDistrict(String districtCode, Consumer<List<Commune>> onSuccess, Consumer<String> onError) {
        apiService.getAllCommune().enqueue(new Callback<List<Commune>>() {
            @Override
            public void onResponse(Call<List<Commune>> call, Response<List<Commune>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Commune> allCommune = response.body();
                    List<Commune> filteredCommune = new ArrayList<>();
                    for (Commune commune : allCommune) {
                        if (commune.getIdDistrict().equals(districtCode)) {
                            filteredCommune.add(commune);
                        }
                    }
                    onSuccess.accept(filteredCommune);
                } else {
                    onError.accept("Lỗi khi tải xã: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Commune>> call, Throwable t) {
                onError.accept("Lỗi khi tải xã: " + t.getMessage());
            }
        });
    }
}
