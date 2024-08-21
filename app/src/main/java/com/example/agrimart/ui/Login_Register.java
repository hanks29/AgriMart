package com.example.agrimart.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.R;
import com.example.agrimart.adapter.LoginRegisterPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Login_Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#018241"));

        ViewPager2 viewPager = findViewById(R.id.viewPagerLR);
        TabLayout tabLayout = findViewById(R.id.tabLayoutLR);

        viewPager.setAdapter(new LoginRegisterPagerAdapter(this)); // Sử dụng 'this' thay vì 'requireActivity()'
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getTabTitle(position))
        ).attach();
    }

    private String getTabTitle(int position) {
        switch (position) {
            case 0:
                return "Đăng nhập";
            case 1:
                return "Đăng ký";
            default:
                return null;
        }
    }
}
