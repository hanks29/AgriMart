package com.example.agrimart.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.ApiClient;
import com.example.agrimart.data.interface1.ApiService;
import com.example.agrimart.data.model.Commune;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.Province;
import com.example.agrimart.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileStoreViewModel extends ViewModel {
    public MutableLiveData<String> nameStore = new MutableLiveData<>("");
    public MutableLiveData<String> phoneNumber = new MutableLiveData<>("");

    public MutableLiveData<List<Province>> provinces = new MutableLiveData<>();
    public MutableLiveData<List<District>> districts = new MutableLiveData<>();
    public MutableLiveData<List<Commune>> wards = new MutableLiveData<>();

    public MutableLiveData<String> province = new MutableLiveData<>();
    public MutableLiveData<String> district = new MutableLiveData<>();
    public MutableLiveData<String> ward = new MutableLiveData<>();
    public MutableLiveData<String> street = new MutableLiveData<>("");
    private MutableLiveData<String> storeImage = new MutableLiveData<>();
    private final ApiService apiService;

    public EditProfileStoreViewModel() {
        apiService = ApiClient.getClient().create(ApiService.class);
//        getProfileStore();
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

    public MutableLiveData<String> getNameStore() {
        return nameStore;
    }

    public void setNameStore(MutableLiveData<String> nameStore) {
        this.nameStore = nameStore;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(MutableLiveData<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MutableLiveData<String> getStreet() {
        return street;
    }

    public void setStreet(MutableLiveData<String> street) {
        this.street = street;
    }


    public void getProfileStore(){ {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                                User user1=documentSnapshot.toObject(User.class);
                                nameStore.setValue(user1.getStoreName());
                                phoneNumber.setValue(user1.getPhoneNumberStore());
                                street.setValue(user1.getStoreAddress().getStreet());
                                province.setValue(user1.getStoreAddress().getCity());
                                district.setValue(user1.getStoreAddress().getDistrict());
                                ward.setValue(user1.getStoreAddress().getWard());
                                storeImage.setValue(user1.getStoreImage());

                        }
                    });
                    }
        }
    }


}
