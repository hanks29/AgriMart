package com.example.agrimart.ui.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.ui.MainActivity;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.PurchasedOrdersActivity;
import com.example.agrimart.viewmodel.CheckoutViewModel;

public class PlaceOrderActivity extends AppCompatActivity {

    private CheckoutViewModel checkoutViewModel;
    private Button btnReturnToHome, btnViewOrders;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(v -> navigateToMainActivity());

        btnReturnToHome = findViewById(R.id.btnReturnToHome);
        btnViewOrders = findViewById(R.id.btnViewOrders);

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        btnReturnToHome.setOnClickListener(v -> navigateToMainActivity());

        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceOrderActivity.this, PurchasedOrdersActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(PlaceOrderActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToMainActivity();
    }
}