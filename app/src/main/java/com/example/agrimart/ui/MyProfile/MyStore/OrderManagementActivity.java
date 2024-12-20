package com.example.agrimart.ui.MyProfile.MyStore;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.R;
import com.example.agrimart.adapter.StateOrderAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderManagementActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TabLayout tabLayout=findViewById(R.id.tabLayout);
        MaterialToolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        ViewPager2 viewPager2=findViewById(R.id.vpStateOrder);
        StateOrderAdapter stateOrderAdapter = new StateOrderAdapter(this);
        viewPager2.setAdapter(stateOrderAdapter);
//        binding.vpStateOrder.setAdapter(stateOrderAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0: {
                    tab.setText("Chờ xác nhận");
                    break;
                }
                case 1: {
                    tab.setText("Chờ lấy hàng");
                    break;
                }
                case 2: {
                    tab.setText("Đang vận chuyển");
                    break;
                }
                case 3: {
                    tab.setText("Đã giao");
                    break;
                }
                case 4: {
                    tab.setText("Trả hàng");
                    break;
                }
            }
        }).attach();


    }
}