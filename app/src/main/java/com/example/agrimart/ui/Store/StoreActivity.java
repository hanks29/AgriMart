package com.example.agrimart.ui.Store;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.adapter.ProductAdapter;
import com.example.agrimart.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private ImageView storeAvatar;
    private TextView storeName, storeRating, storeAddress;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_store);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        }

        storeAvatar = findViewById(R.id.store_avatar);
        storeName = findViewById(R.id.store_name);
        storeRating = findViewById(R.id.store_rating);
        storeAddress = findViewById(R.id.store_address);
        rvProducts = findViewById(R.id.rvProducts);

        storeName.setText("Khánh");
        storeRating.setText("Đánh giá: 4.5");
        storeAddress.setText("123 Lê Trong Tấn, Tân Phú, HCM");

        productList = new ArrayList<>();
        productList.add(new Product(R.drawable.apple, "Táo", "50000"));
        productList.add(new Product(R.drawable.frash_fruits, "ABC", "20000"));
        productList.add(new Product(R.drawable.vegetable, "XYZ", "30000"));

        productAdapter = new ProductAdapter(productList, product -> {

        });
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
    }
}