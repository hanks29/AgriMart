package com.example.agrimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.agrimart.R;
import com.example.agrimart.ui.Cart.CartFragment;
import com.example.agrimart.ui.Homepage.HomeFragment;
import com.example.agrimart.ui.Notification.NotificationActivity;
import com.example.agrimart.ui.MyProfile.MyProfileFragment;
import com.example.agrimart.ui.PostProduct.PostProductActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ImageButton btnNotification;
    ConstraintLayout header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

        addControls();
        addEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.home) {
            selectedFragment = new HomeFragment();
        }
        else if (item.getItemId() == R.id.post) {
            Intent intent = new Intent(MainActivity.this, PostProductActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.profile) {
            selectedFragment = new MyProfileFragment();
        }
            return true;
        } else if (item.getItemId() == R.id.cart) {
            selectedFragment = new CartFragment();
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        return true;
    }

    void addControls()
    {
        btnNotification = findViewById(R.id.btnNotification);
    }

    void addEvents()
    {
        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }
}