package com.example.agrimart.ui.MyProfile.MyStore;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.ui.PostProduct.PostProductActivity;

public class MyStoreActivity extends AppCompatActivity {

    CardView postProduct, orderManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        }

        addControl();
        addEvent();
    }



    private void addControl() {
        postProduct = findViewById(R.id.postProduct);
        orderManagement = findViewById(R.id.orderManagement);
    }

    private void addEvent() {
        postProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MyStoreActivity.this, PostProductActivity.class);
            startActivity(intent);
        });

        orderManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MyStoreActivity.this, OrderManagementActivity.class);
            startActivity(intent);
        });
    }

}

