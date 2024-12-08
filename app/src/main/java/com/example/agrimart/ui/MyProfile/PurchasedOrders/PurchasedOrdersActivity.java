package com.example.agrimart.ui.MyProfile.PurchasedOrders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.agrimart.R;
import com.example.agrimart.adapter.ViewPagerAdapter;
import com.example.agrimart.data.model.Order;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PurchasedOrdersActivity extends AppCompatActivity {
    private static final String TAG = "PurchasedOrdersActivity"; // Thêm hằng số TAG cho log
    private static final int REQUEST_CODE_RATING = 1001;

    private ViewPager2 viewPager;
    private List<Fragment> fragments;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RATING) {
            if (resultCode == RESULT_OK) {
                String orderStatus = data.getStringExtra("order_status");
                assert orderStatus != null;
                loadFragmentAtPosition(location(orderStatus), orderStatus);
            }
        }

    }



    private void loadFragmentAtPosition(int position, String orderStatus) {
        if (position >= 0 && position < fragments.size()) {
            // Lấy Fragment tại vị trí cần load lại
            Fragment newFragment = new OrderStatusFragment(orderStatus);

            // Thay thế Fragment hiện tại tại vị trí đó
            fragments.set(position , newFragment);

            // Cập nhật lại adapter với danh sách fragment mới
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments, Arrays.asList("Chờ xác nhận","Chờ lấy hàng", "Chờ giao hàng", "Trả hàng", "Đã giao", "Đã hủy"));
            viewPager.setAdapter(adapter);


            viewPager.setCurrentItem(position, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_orders);

        // Khởi tạo TabLayout và ViewPager2
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        ImageButton btn_back = findViewById(R.id.btn_back);

        // Tạo danh sách Fragment và trạng thái
        fragments = new ArrayList<>();
        List<String> titles = Arrays.asList("Chờ xác nhận", "Chờ lấy hàng", "Chờ giao hàng", "Trả hàng" ,"Đã giao", "Đã hủy");
        List<String> statuses = Arrays.asList("pending", "approved", "delivering", "return" ,"delivered", "canceled");

        for (String status : statuses) {
            fragments.add(new OrderStatusFragment(status)); // Thêm các fragment vào danh sách
        }

        // Gán adapter cho ViewPager2
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragments, titles);
        viewPager.setAdapter(viewPagerAdapter);

        // Thiết lập TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, pos) -> tab.setText(titles.get(pos))).attach();

        int selectedTab = getIntent().getIntExtra("selectedTab", 0); // 0 là giá trị mặc định
        if (selectedTab >= 0 && selectedTab < fragments.size()) {
            viewPager.setCurrentItem(selectedTab, true); // Thiết lập tab được chọn
        }

        btn_back.setOnClickListener(v -> onBackPressed());
    }

    private int location (String status)
    {
        switch (status){
            case "pending": return 0;
            case "approved": return 1;
            case "delivery": return 2;
            case "refund": return 3;
            case "delivered": return 4;
            case "canceled" : return 5;
        }
        return -1;
    }
}

