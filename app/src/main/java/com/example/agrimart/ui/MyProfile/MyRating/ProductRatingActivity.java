package com.example.agrimart.ui.MyProfile.MyRating;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;

import java.util.List;

public class ProductRatingActivity extends AppCompatActivity {

    ImageView productImage;
    TextView txtProductName, txtQuantity;
    Order order;
    List<Product> product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_rating);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        order = (Order) getIntent().getSerializableExtra("order");
        product = order.getProducts();

        addControl();
        addEvent();

    }

    void addControl()
    {
        productImage = findViewById(R.id.product_image);
        txtProductName = findViewById(R.id.txt_productName);
        txtQuantity = findViewById(R.id.txt_quantity);
    }

    void addEvent()
    {
        txtProductName.setText(order.getStoreName());
    }
}