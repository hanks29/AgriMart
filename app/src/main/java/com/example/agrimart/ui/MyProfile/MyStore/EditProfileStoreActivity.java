package com.example.agrimart.ui.MyProfile.MyStore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
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
    private Uri storeImageUri;
    private String selectedProvinceName="";
    private String provinceId;
    private String selectedDistrictName="";
    private String districtId;
    private String selectedWardName="";


    private String storeName="";
    private String phoneNumber="";
    private String street="";


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
        disableSpinner();
        viewModel.getProfileStore();
        loadProvinces();
        binding.btnEdit.setOnClickListener(v -> {
            enableSpinner();
        });
        viewModel.nameStore.observe(this, nameStore -> {
            binding.edtNameStore.setText(nameStore);
        });
        viewModel.phoneNumber.observe(this, phoneNumber -> {
            binding.edtPhoneNumber.setText(phoneNumber);
        });
        viewModel.street.observe(this, street -> {
            binding.edtStreet.setText(street);
        });
        viewModel.storeImage.observe(this, storeImage -> {
            Glide.with(this).load(storeImage).into(binding.imgAvt);
        });
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Glide.with(this)
                                .load(uri).
                                into(binding.imgAvt);
                        imageUri=uri;
                        Log.d("PhotoPicker", "Selected URI: " + uri.toString());
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
        binding.btnUpdate.setOnClickListener(v -> {
            storeName=binding.edtNameStore.getText().toString();
            phoneNumber=binding.edtPhoneNumber.getText().toString();
            street=binding.edtStreet.getText().toString();
            viewModel.updateProfile(storeName,phoneNumber,street,selectedProvinceName,selectedDistrictName,selectedWardName,imageUri);
            Intent intent=new Intent(EditProfileStoreActivity.this, MyStoreActivity.class);
            startActivity(intent);
        });
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void disableSpinner(){
        binding.spinnerCity.setEnabled(false);
        binding.spinnerDistrict.setEnabled(false);
        binding.spinnerWard.setEnabled(false);
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
            String selectedProvince = viewModel.province.getValue();
            if (selectedProvince != null) {
                binding.spinnerCity.setSelection(findProvincePosition(provinceNames, selectedProvince));

            }
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
            String selectedDistrict = viewModel.district.getValue();
            if (selectedDistrict != null) {
                binding.spinnerDistrict.setSelection(findDistrictPosition(districtNames, selectedDistrict));
            }
        });
    }

    private void loadWard(String districtId){
        viewModel.getWard(districtId);
        viewModel.wards.observe(this, wards -> {
            List<String> wardNames=wards.stream().map(ward -> ward.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerWard,wardNames,position -> selectedWardName=wards.get(position).getName());
            String selectedWard = viewModel.ward.getValue();
            if (selectedWard != null) {
                binding.spinnerWard.setSelection(findWardPosition(wardNames, selectedWard));
            }
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

    private void updateProfile(){
        storeName=binding.edtNameStore.getText().toString();
        phoneNumber=binding.edtPhoneNumber.getText().toString();
        street=binding.edtStreet.getText().toString();
        viewModel.updateProfile(storeName,phoneNumber,street,selectedProvinceName,selectedDistrictName,selectedWardName,imageUri);
    }

}