package com.example.agrimart.ui.MyProfile.MyAddress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.data.model.District;
import com.example.agrimart.data.model.Province;
import com.example.agrimart.data.model.Commune;
import com.example.agrimart.viewmodel.GetAPIAddressViewModel;

import java.util.ArrayList;
import java.util.List;

public class GetAddressActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private GetAPIAddressViewModel getAPIAddressModel;
    private TextView txtChon;
    private String selectedProvinceName, selectedProvinceId;
    private String selectedDistrictName, selectedDistrictId;
    private String selectedCommuneName, selectedCommuneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_apiaddress);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getAPIAddressModel = new GetAPIAddressViewModel();

        initializeViews();

        // Kiểm tra dữ liệu từ Intent
        Intent intent = getIntent();
        String detailedAddressID = intent.getStringExtra("detailedAddressID");
        if (detailedAddressID != null && !detailedAddressID.isEmpty()) {
            String[] ids = detailedAddressID.split(",");
            if (ids.length == 3) {
                selectedProvinceId = ids[0];
                selectedDistrictId = ids[1];
                selectedCommuneId = ids[2];
                prepopulateAddress(selectedProvinceId, selectedDistrictId, selectedCommuneId);
            }
        } else {
            loadProvinces();
        }

        setEventListeners();
    }


    private void initializeViews() {
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        btnBack = findViewById(R.id.btn_back);
        txtChon = findViewById(R.id.txt_chon);
    }

    private void setEventListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        txtChon.setOnClickListener(v -> {
            String address = selectedProvinceName + ", " + selectedDistrictName + ", " + selectedCommuneName;
            String id = selectedProvinceId + "," + selectedDistrictId + "," + selectedCommuneId;
            Intent intent = new Intent();
            intent.putExtra("address", address);
            intent.putExtra("detailedAddressID", id);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void loadProvinces() {
        getAPIAddressModel.loadProvinces(provinceApiResponses -> {
            List<String> provinceNames = new ArrayList<>();
            for (Province provinceResponse : provinceApiResponses) {
                provinceNames.add(provinceResponse.getName());
            }
            setupSpinner(spinnerProvince, provinceNames, position -> {
                selectedProvinceName = provinceApiResponses.get(position).getName();
                selectedProvinceId = provinceApiResponses.get(position).getIdProvince();
                loadDistrictsByProvince(selectedProvinceId);
            });
        }, error -> Log.e("API Error", error));
    }

    private void prepopulateAddress(String provinceId, String districtId, String communeId) {
        getAPIAddressModel.loadProvinces(provinceApiResponses -> {
            List<String> provinceNames = new ArrayList<>();
            int provincePosition = -1;
            for (int i = 0; i < provinceApiResponses.size(); i++) {
                Province province = provinceApiResponses.get(i);
                provinceNames.add(province.getName());
                if (province.getIdProvince().equals(provinceId)) {
                    selectedProvinceName = province.getName();
                    provincePosition = i;
                }
            }
            int finalProvincePosition = provincePosition;
            setupSpinner(spinnerProvince, provinceNames, position -> {
                if (position == finalProvincePosition) {
                    loadDistrictsByProvince(provinceId);
                } else {
                    selectedProvinceName = provinceApiResponses.get(position).getName();
                    selectedProvinceId = provinceApiResponses.get(position).getIdProvince();
                    loadDistrictsByProvince(selectedProvinceId);
                }
            });
            if (provincePosition != -1) {
                spinnerProvince.setSelection(provincePosition);
            }
        }, error -> Log.e("API Error", error));
    }

    private void loadDistrictsByProvince(String provinceCode) {
        getAPIAddressModel.loadDistrictsByProvince(provinceCode, districts -> {
            List<String> districtNames = new ArrayList<>();
            int districtPosition = -1;
            for (int i = 0; i < districts.size(); i++) {
                District district = districts.get(i);
                districtNames.add(district.getName());
                if (district.getIdDistrict().equals(selectedDistrictId)) {
                    selectedDistrictName = district.getName();
                    districtPosition = i;
                }
            }
            int finalDistrictPosition = districtPosition;
            setupSpinner(spinnerDistrict, districtNames, position -> {
                if (position == finalDistrictPosition) {
                    loadCommuneByDistrict(selectedDistrictId);
                } else {
                    selectedDistrictName = districts.get(position).getName();
                    selectedDistrictId = districts.get(position).getIdDistrict();
                    loadCommuneByDistrict(selectedDistrictId);
                }
            });
            if (districtPosition != -1) {
                spinnerDistrict.setSelection(districtPosition);
            }
        }, error -> Log.e("API Error", error));
    }

    private void loadCommuneByDistrict(String districtCode) {
        getAPIAddressModel.loadCommuneByDistrict(districtCode, communeList -> {
            List<String> communeNames = new ArrayList<>();
            int communePosition = -1;
            for (int i = 0; i < communeList.size(); i++) {
                Commune commune = communeList.get(i);
                communeNames.add(commune.getName());
                if (commune.getIdCommune().equals(selectedCommuneId)) {
                    selectedCommuneName = commune.getName();
                    communePosition = i;
                }
            }
            setupSpinner(spinnerWard, communeNames, position -> {
                selectedCommuneName = communeList.get(position).getName();
                selectedCommuneId = communeList.get(position).getIdCommune();
            });
            if (communePosition != -1) {
                spinnerWard.setSelection(communePosition);
            }
        }, error -> Log.e("API Error", error));
    }



    private void setupSpinner(Spinner spinner, List<String> names, OnItemSelectedListener onItemSelected) {
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
}
