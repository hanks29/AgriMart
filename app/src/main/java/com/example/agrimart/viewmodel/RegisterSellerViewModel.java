package com.example.agrimart.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.API.ApiClient;
import com.example.agrimart.data.interface1.ApiService;
import com.example.agrimart.data.model.Commune;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.Province;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterSellerViewModel extends ViewModel {

    private ApiService apiService;
    public MutableLiveData<List<Province>> provinces = new MutableLiveData<>();
    public MutableLiveData<List<District>> districts = new MutableLiveData<>();
    public MutableLiveData<List<Commune>> wards = new MutableLiveData<>();

    public RegisterSellerViewModel() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void getProvinces() {
        apiService.getAllProvinces().enqueue(new Callback<List<Province>>() {
            @Override
            public void onResponse(Call<List<Province>> call, Response<List<Province>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Log.d("RegisterSellerViewModel", response.body().toString());
                    provinces.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Province>> call, Throwable t) {
                Toast.makeText(null, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getDistricts(String provinceId) {
        apiService.getAllDistricts().enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Log.d("RegisterSellerViewModel", response.body().toString());
                    List<District> districtList = response.body();
                    List<District> districtList1 = districtList
                            .stream().filter(district->district.getIdProvince().equals(provinceId))
                            .collect(Collectors.toList());
                    districts.setValue(districtList1);
                }
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                Toast.makeText(null, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getWard(String districtId){
            apiService.getAllCommune().enqueue(new Callback<List<Commune>>() {
                @Override
                public void onResponse(Call<List<Commune>> call, Response<List<Commune>> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        List<Commune> communeList = response.body();
                        List<Commune> communeList1 = communeList
                                .stream().filter(commune->commune.getIdDistrict().equals(districtId))
                                .collect(Collectors.toList());
                        wards.setValue(communeList1);
                    }
                }

                @Override
                public void onFailure(Call<List<Commune>> call, Throwable t) {
                    Toast.makeText(null, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }


}
