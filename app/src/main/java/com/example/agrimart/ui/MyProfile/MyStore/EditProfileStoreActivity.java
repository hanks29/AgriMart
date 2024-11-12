package com.example.agrimart.ui.MyProfile.MyStore;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.Province;
import com.example.agrimart.databinding.ActivityEditProfileStoreBinding;
import com.example.agrimart.viewmodel.EditProfileStoreViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class EditProfileStoreActivity extends AppCompatActivity {

    private EditProfileStoreViewModel viewModel;
    private ActivityEditProfileStoreBinding binding;

    private Uri imageUri;
    private String selectedProvinceName="";
    private String provinceId;
    private String selectedDistrictName="";
    private String districtId;
    private String selectedWardName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewModel=new ViewModelProvider(this).get(EditProfileStoreViewModel.class);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_edit_profile_store);
        binding.setViewModel(viewModel);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadProvinces();
        enableSpinner();
        viewModel.getProfileStore();
//        loadProvinces();
//        viewModel.province.observe(this,province -> {
//            if(!province.isEmpty()){
//                binding.spinnerCity.setSelection(findProvincePosition(viewModel.provinces.getValue().stream().map(province1 -> province1.getName()).collect(Collectors.toList()),province));
//            }
//        });
//        viewModel.district.observe(this,district -> {
//            if(!district.isEmpty()){
//                binding.spinnerDistrict.setSelection(findDistrictPosition(viewModel.districts.getValue().stream().map(district1 -> district1.getName()).collect(Collectors.toList()),district));
//            }
//        });
//        viewModel.ward.observe(this,ward -> {
//            if(!ward.isEmpty()){
//                binding.spinnerWard.setSelection(findWardPosition(viewModel.wards.getValue().stream().map(ward1 -> ward1.getName()).collect(Collectors.toList()),ward));
//            }
//        });


    }

    private void enableSpinner(){
        binding.spinnerCity.setEnabled(true);
        binding.spinnerDistrict.setEnabled(true);
        binding.spinnerWard.setEnabled(true);
    }


    private void loadProvinces(){
        viewModel.getProvinces();
        viewModel.provinces.observe(this, provinces -> {
            List<String> provinceNames = provinces.stream().map(province -> province.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerCity, provinceNames, position -> {
                provinceId=provinces.get(position).getIdProvince();
                selectedProvinceName = provinces.get(position).getName();
                loadDistrict(provinceId);
            });
        });


    }
    private void loadDistrict(String provinceId){
        viewModel.getDistricts(provinceId);
        viewModel.districts.observe(this, districts -> {
            List<String> districtNames=districts.stream().map(district -> district.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerDistrict,districtNames,position -> {
                districtId=districts.get(position).getIdDistrict();
                selectedDistrictName=districts.get(position).getName();
                loadWard(districtId);
            });
        });
    }

    private void loadWard(String districtId){
        viewModel.getWard(districtId);
        viewModel.wards.observe(this, wards -> {
            List<String> wardNames=wards.stream().map(ward -> ward.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerWard,wardNames,position -> selectedWardName=wards.get(position).getName());
        });
    }

    private void setupSpinner(Spinner spinner, List<String> names, EditProfileStoreActivity.OnItemSelectedListener onItemSelected) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected.onItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }
    private interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private int findProvincePosition(List<String> provinces, String provinceName) {
        for (int i = 0; i < provinces.size(); i++) {
            if (provinces.get(i).equals(provinceName)) {
                return i;
            }
        }
        return 0;
    }

    private int findDistrictPosition(List<String> districts, String districtName) {
        for (int i = 0; i < districts.size(); i++) {
            if (districts.get(i).equals(districtName)) {
                return i;
            }
        }
        return 0;
    }

    private int findWardPosition(List<String> wards, String wardName) {
        for (int i = 0; i < wards.size(); i++) {
            if (wards.get(i).equals(wardName)) {
                return i;
            }
        }
        return 0;
    }

}