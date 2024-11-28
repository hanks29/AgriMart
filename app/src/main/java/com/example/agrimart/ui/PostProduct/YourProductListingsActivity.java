package com.example.agrimart.ui.PostProduct;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.R;
import com.example.agrimart.adapter.StatusProductAdapter;
import com.example.agrimart.databinding.ActivityYourProductListingsBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class YourProductListingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_product_listings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ImageButton btn_backPostList = findViewById(R.id.btn_backPostList);
        btn_backPostList.setOnClickListener(v -> {
            finish();
        });
        TabLayout tabLayout=findViewById(R.id.tabLayout);
        ViewPager2 viewPager2=findViewById(R.id.vpStateProduct);
        StatusProductAdapter statusProductAdapter = new StatusProductAdapter(this);
        viewPager2.setAdapter(statusProductAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0: {
                    tab.setText("Đang bán");
                    break;
                }
                case 1: {
                    tab.setText("Chờ duyệt");
                    break;
                }
                case 2: {
                    tab.setText("Đã bị hủy");
                    break;
                }
            }
        }).attach();

    }

//    public void changeFragment(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container_post_product, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }


}
