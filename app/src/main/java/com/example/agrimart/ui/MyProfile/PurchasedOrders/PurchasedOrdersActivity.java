package com.example.agrimart.ui.MyProfile.PurchasedOrders;

import android.os.Bundle;
import android.util.Log; // Thêm import cho Log
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.agrimart.R;
import com.example.agrimart.data.adapter.PurchasedOrdersPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class PurchasedOrdersActivity extends AppCompatActivity {
    private static final String TAG = "PurchasedOrdersActivity"; // Thêm hằng số TAG cho log


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_orders);

        // Khởi tạo TabLayout và ViewPager2
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        ImageButton btn_back = findViewById(R.id.btn_back);


        // Tạo adapter cho ViewPager2
        PurchasedOrdersPagerAdapter pagerAdapter = new PurchasedOrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Kết nối TabLayout với ViewPager2 thông qua TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Chờ xác nhận");
                    break;
                case 1:
                    tab.setText("Chờ lấy hàng");
                    break;
                case 2:
                    tab.setText("Chờ giao hàng");
                    break;
                case 3:
                    tab.setText("Đã giao");
                    break;
                case 4:
                    tab.setText("Đã hủy");
                    break;
                case 5:
                    tab.setText("Trả hàng");
                    break;
            }
        }).attach();

        // Lấy giá trị tab được chọn từ Intent
        int selectedTab = getIntent().getIntExtra("selectedTab", 0);

        // Ghi log giá trị selectedTab
        Log.d(TAG, "Selected tab: " + selectedTab);

        // Sử dụng post để đảm bảo giao diện đã sẵn sàng trước khi chuyển tab
        viewPager.post(() -> {
            if (selectedTab >= 0 && selectedTab < viewPager.getAdapter().getItemCount()) {
                viewPager.setCurrentItem(selectedTab, false);
                Log.d(TAG, "Navigated to tab: " + selectedTab);
            } else {
                Log.d(TAG, "Invalid tab index: " + selectedTab);
            }
        });

        btn_back.setOnClickListener(v -> onBackPressed());
    }
}
