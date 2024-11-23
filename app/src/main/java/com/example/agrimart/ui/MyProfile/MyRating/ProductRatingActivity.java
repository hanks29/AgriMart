package com.example.agrimart.ui.MyProfile.MyRating;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ProductReviewAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.ProductReview;

import java.util.ArrayList;
import java.util.List;

public class ProductRatingActivity extends AppCompatActivity {

    TextView txtGui;
    Order order;
    RecyclerView recyclerView;
    List<Product> products;
    ProductReviewAdapter adapter;
    List<ProductReview> listProductReview;
    ImageButton btnBack;

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

        listProductReview = new ArrayList<>();
        order = (Order) getIntent().getSerializableExtra("order");
        products = order.getProducts();

        for (Product product : products) {
            ProductReview productReview = new ProductReview();

            productReview.setImageResId(product.getImages().get(0));
            productReview.setProductName(product.getName());
            productReview.setQuantity(product.getQuantity());
            productReview.setProductId(product.getProduct_id());


            listProductReview.add(productReview);
        }


        addControl();
        setupRecyclerView();
        addEvent();

    }

    void addControl() {
        recyclerView = findViewById(R.id.productRating);
        btnBack = findViewById(R.id.btn_back);
        txtGui = findViewById(R.id.txt_gui);
    }

    void setupRecyclerView() {
        adapter = new ProductReviewAdapter(this, listProductReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    void addEvent() {
        btnBack.setOnClickListener(v -> finish());
        txtGui.setOnClickListener(v -> saveRating());
    }

    private void saveRating() {

    }
}