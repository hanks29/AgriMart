package com.example.agrimart.ui.MyProfile.MyAddress;

import android.content.Intent;
import android.os.Bundle;
import java.util.UUID;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Address;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewAddressActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_NEW_ADDRESS = 1;
    private EditText edtHoTen, edtSDT, edtTenDuong;
    private Switch switch1; // Switch để đánh dấu địa chỉ mặc định
    private TextView btnComplete, edtTinh;
    private FirebaseAuth auth;// TextView cho nút hoàn thành
    private FirebaseFirestore firestore; // Tham chiếu đến Firestore
    private String userId; // ID của người dùng hiện tại
    private LinearLayout address;
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        // Khởi tạo tham chiếu đến Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        addEvent();
    }

    private void addControl()
    {
        // Khởi tạo các View
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSDT = findViewById(R.id.edtSDT);
        edtTinh = findViewById(R.id.edtTinh);
        edtTenDuong = findViewById(R.id.edtTenDuong);
        btnComplete = findViewById(R.id.textView4);
        switch1 = findViewById(R.id.switch1);
        address = findViewById(R.id.id_address);
        btn_back = findViewById(R.id.btn_back);
    }

    private void addEvent()
    {
        // Thiết lập sự kiện click cho nút hoàn thành
        btnComplete.setOnClickListener(v -> uploadAddress());
        btn_back.setOnClickListener(v -> onBackPressed());
        address.setOnClickListener(v -> openAddressAPI());
    }

    private void openAddressAPI() {
        Intent intent = new Intent(NewAddressActivity.this, GetAddressActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NEW_ADDRESS && resultCode == RESULT_OK) {
            if (data != null) {
                // Nhận chuỗi từ Intent
                String addressString = data.getStringExtra("address");
                // Xử lý chuỗi nhận được ở đây (ví dụ: hiển thị trong một TextView hoặc sử dụng nó theo cách khác)
                edtTinh.setText(addressString);
            }
        }
    }

    private void uploadAddress() {
        // Lấy dữ liệu từ các EditText
        String name = edtHoTen.getText().toString().trim();
        String phone = edtSDT.getText().toString().trim();
        String street = edtTenDuong.getText().toString().trim();
        String city = edtTinh.getText().toString().trim();
        String AddressId = generateUniqueAddressId();

        Log.d("NewAddressActivity", "Generated Address ID: " + AddressId);

        // Kiểm tra thông tin đầu vào
        if (name.isEmpty() || phone.isEmpty() || street.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra nếu là địa chỉ đầu tiên của người dùng
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.contains("addresses")) {
                            List<Map<String, Object>> addressMaps = (List<Map<String, Object>>) document.get("addresses");
                            // Nếu không có địa chỉ nào thì đặt địa chỉ mới là mặc định
                            boolean isFirstAddress = (addressMaps == null || addressMaps.isEmpty());
                            boolean isDefault = isFirstAddress || switch1.isChecked();

                            // Tạo một đối tượng địa chỉ mới
                            Address newAddress = new Address(AddressId, name, phone, street, city, isDefault);

                            if (isDefault) {
                                updateAddressesToNotDefault(newAddress);
                            } else {
                                addAddress(newAddress);
                            }
                        } else {
                            // Nếu không có địa chỉ nào, đặt địa chỉ mới là mặc định
                            Address newAddress = new Address(AddressId, name, phone, street, city, true);
                            addAddress(newAddress);
                        }
                    } else {
                        Toast.makeText(NewAddressActivity.this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void updateAddressesToNotDefault(Address newAddress) {
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.contains("addresses")) {
                            List<Map<String, Object>> addressMaps = (List<Map<String, Object>>) document.get("addresses");
                            List<Address> updatedAddresses = new ArrayList<>();

                            for (Map<String, Object> addressMap : addressMaps) {
                                Address address = new Address();
                                address.setAddressId((String) addressMap.get("AddressId"));
                                address.setStreet((String) addressMap.get("street"));
                                address.setPhone((String) addressMap.get("phone"));
                                address.setDetailedAddress((String) addressMap.get("detailedAddress"));
                                address.setName((String) addressMap.get("name"));
                                address.setDefault(false);
                                updatedAddresses.add(address);
                            }

                            updatedAddresses.add(newAddress);
                            updateFirestore(updatedAddresses);
                        }
                    }
                });
    }

    private void addAddress(Address newAddress) {
        firestore.collection("users").document(userId)
                .update("addresses", FieldValue.arrayUnion(newAddress))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NewAddressActivity.this, "Địa chỉ đã được thêm!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewAddressActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFirestore(List<Address> updatedAddresses) {
        firestore.collection("users").document(userId)
                .update("addresses", updatedAddresses)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NewAddressActivity.this, "Địa chỉ đã được thêm!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewAddressActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String generateUniqueAddressId() {
        return UUID.randomUUID().toString();
    }
}
