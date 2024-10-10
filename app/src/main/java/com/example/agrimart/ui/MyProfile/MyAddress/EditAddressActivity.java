package com.example.agrimart.ui.MyProfile.MyAddress;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Address;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAddressActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_NEW_ADDRESS = 1;
    private EditText edtHoTen, edtSDT, edtTinh, edtTenDuong;
    private Switch switchDefault;
    private String AddressId; // Biến lưu trữ ID của địa chỉ
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private LinearLayout save_address, delete_address;

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

        // Khởi tạo các EditText và Switch
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSDT = findViewById(R.id.edtSDT);
        edtTinh = findViewById(R.id.edtTinh);
        edtTenDuong = findViewById(R.id.edtTenDuong);
        switchDefault = findViewById(R.id.switch2);

        // Khởi tạo Firestore và FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Nhận AddressId từ Intent
        Intent intent = getIntent();
        AddressId = intent.getStringExtra("addressId");
        Log.d("EditAddressActivity", "Address ID: " + AddressId);

        // Tải thông tin địa chỉ
        loadAddressDetails();

        // Thiết lập nút quay lại
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập sự kiện cho nút lưu
        save_address = findViewById(R.id.id_save_address);
        delete_address = findViewById(R.id.id_delete_address);

        save_address.setOnClickListener(v -> saveAddress());

        delete_address.setOnClickListener(v -> deleteAddress());
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
                                edtTinh.setText((String) address.get("street"));
                                edtTenDuong.setText((String) address.get("detailedAddress"));

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
        String street = edtTinh.getText().toString();
        String detailedAddress = edtTenDuong.getText().toString();
        boolean isDefault = switchDefault.isChecked();

        Map<String, Object> updatedAddress = new HashMap<>();
        updatedAddress.put("addressId", AddressId); // Giữ lại AddressId
        updatedAddress.put("street", street);
        updatedAddress.put("phone", phone);
        updatedAddress.put("detailedAddress", detailedAddress);
        updatedAddress.put("name", name);
        updatedAddress.put("default", isDefault);

        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("addresses")) {
                        List<Map<String, Object>> addressList = (List<Map<String, Object>>) documentSnapshot.get("addresses");

                        if (isDefault) {
                            for (Map<String, Object> address : addressList) {
                                if (address.containsKey("default") && (Boolean) address.get("default")) {
                                    address.put("default", false);
                                }
                            }
                        }

                        // Cập nhật địa chỉ
                        for (int i = 0; i < addressList.size(); i++) {
                            if (AddressId.equals(addressList.get(i).get("id"))) {
                                addressList.set(i, updatedAddress);
                                break;
                            }
                        }

                        if (isDefault) {
                            addressList.remove(updatedAddress); // Xóa khỏi cuối danh sách
                            addressList.add(0, updatedAddress); // Thêm lên đầu
                        }

                        firestore.collection("users")
                                .document(userId)
                                .update("addresses", addressList)
                                .addOnSuccessListener(aVoid -> {
                                    setResult(RESULT_OK);
                                    Toast.makeText(this, "Địa chỉ đã được lưu", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lưu địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
