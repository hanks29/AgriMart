package com.example.agrimart.ui.MyProfile.state_order;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.R;
import com.example.agrimart.data.adapter.StateOrderAdapter;
import com.example.agrimart.databinding.ActivityShopProfileBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ShopProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TabLayout tabLayout=findViewById(R.id.tabLayout);
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
                    tab.setText("Đã giao");
                    break;
                }
                case 3: {
                    tab.setText("Đang giao");
                    break;
                }
            }
        }).attach();


    }
}