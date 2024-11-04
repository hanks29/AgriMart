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
    private String selectedProvinceName;
    private String selectedDistrictName;
    private String selectedWardName;

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
        loadProvinces();
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
            String address = selectedProvinceName + ", " + selectedDistrictName + ", " + selectedWardName;
            Intent intent = new Intent(GetAddressActivity.this, NewAddressActivity.class);
            intent.putExtra("address", address);
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
                loadDistrictsByProvince(provinceApiResponses.get(position).getIdProvince());
            });
        }, error -> Log.e("API Error", error));
    }

    private void loadDistrictsByProvince(String provinceCode) {
        getAPIAddressModel.loadDistrictsByProvince(provinceCode, districts -> {
            List<String> districtNames = new ArrayList<>();
            for (District district : districts) {
                districtNames.add(district.getName());
            }
            setupSpinner(spinnerDistrict, districtNames, position -> {
                selectedDistrictName = districts.get(position).getName();
                loadCommuneByDistrict(districts.get(position).getIdDistrict());
                Log.d("GetAddressActivity", "IdDistrict: " + districts.get(position).getIdDistrict());
            });
        }, error -> Log.e("API Error", error));
    }

    private void loadCommuneByDistrict(String districtCode) {
        getAPIAddressModel.loadCommuneByDistrict(districtCode, commune -> {
            List<String> communeNames = new ArrayList<>();
            for (Commune ward : commune) {
                communeNames.add(ward.getName());
            }
            setupSpinner(spinnerWard, communeNames, position -> selectedWardName = commune.get(position).getName());
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
