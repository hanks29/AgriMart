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
import com.example.agrimart.data.model.Ward;
import com.example.agrimart.viewmodel.GetAPIAddressViewModel;

import java.util.ArrayList;
import java.util.List;

public class GetAddressActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private GetAPIAddressViewModel getAPIAddressModel;
    private TextView txt_chon;
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

        addControl();
        loadProvinces();
        addEvent();
    }

    private void addControl() {
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        btn_back = findViewById(R.id.btn_back);
        txt_chon = findViewById(R.id.txt_chon);
    }

    private void addEvent()
    {
        btn_back.setOnClickListener(v -> onBackPressed());
        txt_chon.setOnClickListener(v -> {
            String address = selectedProvinceName + ", " + selectedDistrictName + ", " + selectedWardName;
            // Tạo một intent để khởi động NewAddressActivity
            Intent intent = new Intent(GetAddressActivity.this, NewAddressActivity.class);
            // Đưa địa chỉ vào intent
            intent.putExtra("address", address);
            // Khởi động activity mới
            setResult(RESULT_OK, intent);
            // Đóng activity hiện tại
            finish();
        });
    }


    private void loadProvinces() {
        getAPIAddressModel.loadProvinces(provinces -> {
            List<String> provinceNames = new ArrayList<>();
            for (Province province : provinces) {
                provinceNames.add(province.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, provinceNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProvince.setAdapter(adapter);

            spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedProvinceName = provinces.get(position).getName();
                    loadDistrictsByProvince(provinces.get(position).getCode());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No action needed
                }
            });
        }, error -> Log.e("API Error", error));
    }

    private void loadDistrictsByProvince(String provinceCode) {
        getAPIAddressModel.loadDistrictsByProvince(provinceCode, districts -> {
            List<String> districtNames = new ArrayList<>();
            for (District district : districts) {
                districtNames.add(district.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, districtNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDistrict.setAdapter(adapter);

            spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedDistrictName = districts.get(position).getName();
                    loadWardsByDistrict(districts.get(position).getCode());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No action needed
                }
            });
        }, error -> Log.e("API Error", error));
    }

    private void loadWardsByDistrict(String districtCode) {
        getAPIAddressModel.loadWardsByDistrict(districtCode, wards -> {
            List<String> wardNames = new ArrayList<>();
            for (Ward ward : wards) {
                wardNames.add(ward.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, wardNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWard.setAdapter(adapter);

            spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedWardName = wards.get(position).getName();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }, error -> Log.e("API Error", error));
    }

}
