package com.example.agrimart.ui.MyProfile.PurchasedOrders;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.agrimart.R;
import com.example.agrimart.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PurchasedOrdersActivity extends AppCompatActivity {
    private static final String TAG = "PurchasedOrdersActivity"; // Thêm hằng số TAG cho log


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_orders);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo TabLayout và ViewPager2
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        ImageButton btn_back = findViewById(R.id.btn_back);

        //------------------------------------------------------------------------------------------
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = Arrays.asList("Chờ xác nhận", "Chờ giao hàng", "Đang giao", "Đã giao", "Đã hủy");
        List<String> statuses = Arrays.asList("pending", "approved", "delivering", "delivered", "cancel");

        int selectedCategoryIndex = -1;

        for (int i = 0; i < statuses.size(); i++) {
            fragments.add(new OrderStatusFragment(statuses.get(i)));
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragments, titles);
        viewPager.setAdapter(viewPagerAdapter);


        new TabLayoutMediator(tabLayout, viewPager, (tab, pos) -> tab.setText(titles.get(pos))).attach();





        btn_back.setOnClickListener(v -> onBackPressed());
    }
}
