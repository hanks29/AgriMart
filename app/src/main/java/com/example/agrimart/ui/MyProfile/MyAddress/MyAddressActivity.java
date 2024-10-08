package com.example.agrimart.ui.MyProfile.MyAddress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.AddressAdapter;
import com.example.agrimart.data.model.Address;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.PurchasedOrdersActivity;

import java.util.ArrayList;
import java.util.List;

public class MyAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private LinearLayout id_add_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        ImageButton btn_back = findViewById(R.id.btn_back);
        // Áp dụng window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        id_add_address = findViewById(R.id.id_add_address);

        // Tạo danh sách địa chỉ
        addressList = new ArrayList<>();
        // Thêm dữ liệu mẫu

        addressList.add(new Address("Hoàng Huy Long", "(+84) 911 902 876",
                "622/10, Đường Cộng Hòa", "Phường 14, Quận 10, TP. Hồ Chí Minh", false));
        addressList.add(new Address("Hoàng Huy Long", "(+84) 911 902 876",
                "622/10, Đường Cộng Hòa", "Phường 14, Quận 10, TP. Hồ Chí Minh", false));
        addressList.add(new Address("Hoàng Huy Long", "(+84) 911 902 876",
                "622/10, Đường Cộng Hòa", "Phường 14, Quận 10, TP. Hồ Chí Minh", false));
        addressList.add(new Address("Hoàng Huy Long", "(+84) 911 902 876",
                "622/10, Đường Cộng Hòa", "Phường 14, Quận 10, TP. Hồ Chí Minh", false));
        addressList.add(new Address("Hoàng Huy Long", "(+84) 911 902 876",
                "622/10, Đường Cộng Hòa", "Phường 14, Quận 10, TP. Hồ Chí Minh", false));
        addressList.add(new Address("Hoàng Huy Long", "(+84) 911 902 876",
                "622/10, Đường Cộng Hòa", "Phường 14, Quận 10, TP. Hồ Chí Minh", true));

        // Khởi tạo adapter
        addressAdapter = new AddressAdapter(this, addressList); // Thêm context

        recyclerView.setAdapter(addressAdapter);

        id_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAddressActivity.this, NewAddressActivity.class);
                startActivity(intent);
            }
        });

        btn_back.setOnClickListener(v -> onBackPressed());
    }
}
