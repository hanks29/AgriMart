package com.example.agrimart.ui.MyProfile.MyAddress;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Address;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAddressActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_EDIT_ADDRESS = 12;
    private EditText edtHoTen, edtSDT, edtTenDuong;
    private Switch switchDefault;
    private String AddressId; // Biến lưu trữ ID của địa chỉ
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private LinearLayout save_address, delete_address, getAddress;
    private String detailedAddressID;
    private TextView edtTinh;
    private ImageButton btnBack;
    private String province;
    private String district;
    private String commune;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_address);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Khởi tạo Firestore và FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        addControl();

        // Nhận AddressId từ Intent
        Intent intent = getIntent();
        AddressId = intent.getStringExtra("addressId");

        // Tải thông tin địa chỉ
        loadAddressDetails();


        addEvent();

    }

    void addControl() {
        // Khởi tạo các EditText và Switch
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSDT = findViewById(R.id.edtSDT);
        edtTinh = findViewById(R.id.edtTinh);
        edtTenDuong = findViewById(R.id.edtTenDuong);
        switchDefault = findViewById(R.id.switch2);
        getAddress = findViewById(R.id.get_address);
        save_address = findViewById(R.id.id_save_address);
        delete_address = findViewById(R.id.id_delete_address);
        btnBack = findViewById(R.id.btn_back);
    }

    void addEvent() {
        save_address.setOnClickListener(v -> saveAddress());

        delete_address.setOnClickListener(v -> deleteAddress());

        btnBack.setOnClickListener(v -> onBackPressed());

        getAddress.setOnClickListener(v -> openAddressAPI());
    }

    private void openAddressAPI() {
        Intent intent = new Intent(EditAddressActivity.this, GetAddressActivity.class);

        if (detailedAddressID != null && !detailedAddressID.isEmpty()) {
            intent.putExtra("detailedAddressID", detailedAddressID);
        }

        startActivityForResult(intent, REQUEST_CODE_EDIT_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_ADDRESS && resultCode == RESULT_OK) {
            if (data != null) {
                // Nhận chuỗi từ Intent
                String addressString = data.getStringExtra("address");
                detailedAddressID = data.getStringExtra("detailedAddressID");
                // Xử lý chuỗi nhận được ở đây (ví dụ: hiển thị trong một TextView hoặc sử dụng nó theo cách khác)
                edtTinh.setText(addressString);
            }
        }
    }

    private void loadAddressDetails() {
        String userId = auth.getCurrentUser().getUid();

        // Lấy thông tin địa chỉ bằng AddressId
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("addresses")) {
                        List<Map<String, Object>> addressList = (List<Map<String, Object>>) documentSnapshot.get("addresses");

                        // Tìm địa chỉ theo AddressId
                        for (Map<String, Object> address : addressList) {
                            if (AddressId.equals(address.get("addressId"))) {
                                edtHoTen.setText((String) address.get("name"));
                                edtSDT.setText((String) address.get("phone"));
                                edtTenDuong.setText((String) address.get("street"));
                                province = (String) address.get("province");
                                district = (String) address.get("district");
                                commune = (String) address.get("commune");

                                edtTinh.setText(commune + ", " + district + ", " + province);
                                detailedAddressID = (String) address.get("detailedAddressID");

                                Boolean isDefault = (Boolean) address.get("default");
                                switchDefault.setChecked(isDefault != null ? isDefault : false);
                                return; // Dừng việc tìm kiếm khi đã tìm thấy địa chỉ
                            }
                        }
                        Log.d("EditAddressActivity", "Address not found with ID: " + AddressId);
                    } else {
                        Log.d("EditAddressActivity", "No addresses field in document.");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveAddress() {
        String name = edtHoTen.getText().toString();
        String phone = edtSDT.getText().toString();
        String street = edtTenDuong.getText().toString();
        boolean isDefault = switchDefault.isChecked();

        Map<String, Object> updatedAddress = new HashMap<>();
        updatedAddress.put("addressId", AddressId);
        updatedAddress.put("street", street);
        updatedAddress.put("phone", phone);
        updatedAddress.put("province", province);
        updatedAddress.put("district", district);
        updatedAddress.put("commune", commune);
        updatedAddress.put("detailedAddressID", detailedAddressID);
        updatedAddress.put("name", name);
        updatedAddress.put("default", isDefault);

        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("addresses")) {
                        List<Map<String, Object>> addressList = (List<Map<String, Object>>) documentSnapshot.get("addresses");

                        // Ensure we update only the matching address
                        boolean addressFound = false;

                        if (isDefault) {
                            // Reset default flag for all addresses
                            for (Map<String, Object> address : addressList) {
                                if (address.containsKey("default") && (Boolean) address.get("default")) {
                                    address.put("default", false);
                                }
                            }
                        }

                        // Update the existing address
                        for (int i = 0; i < addressList.size(); i++) {
                            if (AddressId.equals(addressList.get(i).get("addressId"))) {
                                addressList.set(i, updatedAddress);
                                addressFound = true;
                                break;
                            }
                        }

                        if (addressFound) {
                            // Update Firestore with modified address list
                            firestore.collection("users")
                                    .document(userId)
                                    .update("addresses", addressList)
                                    .addOnSuccessListener(aVoid -> {
                                        setResult(RESULT_OK);
                                        Toast.makeText(this, "Địa chỉ đã được lưu", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lưu địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Không tìm thấy địa chỉ để cập nhật.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải danh sách địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void deleteAddress() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("addresses")) {
                        List<Map<String, Object>> addressList = (List<Map<String, Object>>) documentSnapshot.get("addresses");

                        for (int i = 0; i < addressList.size(); i++) {
                            if (AddressId.equals(addressList.get(i).get("addressId"))) {
                                addressList.remove(i);
                                break;
                            }
                        }

                        firestore.collection("users")
                                .document(userId)
                                .update("addresses", addressList)
                                .addOnSuccessListener(aVoid -> {
                                    setResult(RESULT_OK);
                                    Toast.makeText(this, "Địa chỉ đã được xóa", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xóa địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải danh sách địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
