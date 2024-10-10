package com.example.agrimart.ui.MyProfile.MyAddress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.AddressAdapter;
import com.example.agrimart.data.model.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private LinearLayout id_add_address;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        // Thiết lập nút quay lại
        ImageButton btn_back = findViewById(R.id.btn_back);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Bạn cần đăng nhập để xem địa chỉ của mình.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadAddresses();

        // Thiết lập sự kiện click cho nút thêm địa chỉ
        id_add_address = findViewById(R.id.id_add_address);
        id_add_address.setOnClickListener(v -> startNewAddressActivity());

        // Thiết lập sự kiện click cho nút quay lại
        btn_back.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressList = new ArrayList<>();

        // Truyền listener vào adapter
        addressAdapter = new AddressAdapter(this, addressList, (position) -> {
            // Lấy AddressId từ địa chỉ được chọn
            String AddressId = addressList.get(position).getAddressId();

            // Khởi chạy EditAddressActivity với AddressId
            Intent intent = new Intent(MyAddressActivity.this, EditAddressActivity.class);
            intent.putExtra("addressId", AddressId); // Truyền AddressId
            startActivityForResult(intent, NewAddressActivity.REQUEST_CODE_NEW_ADDRESS);
        });



        recyclerView.setAdapter(addressAdapter);
    }

    private void startNewAddressActivity() {
        Intent intent = new Intent(MyAddressActivity.this, NewAddressActivity.class);
        startActivityForResult(intent, NewAddressActivity.REQUEST_CODE_NEW_ADDRESS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewAddressActivity.REQUEST_CODE_NEW_ADDRESS && resultCode == RESULT_OK) {
            loadAddresses(); // Tải lại danh sách địa chỉ khi có địa chỉ mới được thêm
        }
    }

    private void loadAddresses() {
        Log.d("MyAddressActivity", "Loading addresses for user ID: " + userId);

        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.contains("addresses")) {
                                List<Map<String, Object>> addressMaps = (List<Map<String, Object>>) document.get("addresses");
                                updateAddressList(addressMaps);
                            } else {
                                Log.d("MyAddressActivity", "No addresses field in document for user ID: " + userId);
                            }
                        } else {
                            Log.d("MyAddressActivity", "Document does not exist for user ID: " + userId);
                        }
                    } else {
                        Log.e("MyAddressActivity", "Failed to load addresses: ", task.getException());
                    }
                });
    }

    private void updateAddressList(List<Map<String, Object>> addressMaps) {
        List<Address> defaultAddresses = new ArrayList<>();
        List<Address> nonDefaultAddresses = new ArrayList<>();

        for (Map<String, Object> addressMap : addressMaps) {
            Address address = new Address();
            address.setAddressId((String) addressMap.get("addressId"));
            address.setStreet((String) addressMap.get("street"));
            address.setPhone((String) addressMap.get("phone"));
            address.setDetailedAddress((String) addressMap.get("detailedAddress"));
            address.setName((String) addressMap.get("name"));
            address.setDefault((Boolean) addressMap.get("default"));

            if (address.isDefault()) {
                defaultAddresses.add(address); // Thêm vào danh sách địa chỉ mặc định
            } else {
                nonDefaultAddresses.add(address); // Thêm vào danh sách địa chỉ không mặc định
            }
        }

        // Kết hợp các địa chỉ mặc định và không mặc định
        addressList.clear();
        addressList.addAll(defaultAddresses); // Địa chỉ mặc định lên trước
        addressList.addAll(nonDefaultAddresses); // Địa chỉ không mặc định xuống dưới

        addressAdapter.notifyDataSetChanged();
    }
}
