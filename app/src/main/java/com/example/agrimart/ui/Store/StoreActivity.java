package com.example.agrimart.ui.Store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Store;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.HomeFragmentViewModel;
import com.example.agrimart.viewmodel.ProductDetailViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private ImageView storeAvatar;
    private TextView storeName, storeRating, storeAddress;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private String storeId;
    private ProductDetailViewModel viewModel;
    private HomeFragmentViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        setupWindowInsets();
        addControls();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        storeId = intent.getStringExtra("storeId");
        Log.d("StoreActivity", "Store ID: " + storeId);

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        productViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, product -> {
            Intent productIntent = new Intent(StoreActivity.this, ProductDetailActivity.class);
            productIntent.putExtra("product", (Parcelable) product);
            startActivity(productIntent);
        });

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);

        viewModel.getStoreInfo().observe(this, store -> {
            if (store != null) {
                updateStoreInfo(store);
            }
        });

        loadStoreInfo();
        loadProducts(storeId);
    }

    private void setupWindowInsets() {
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
    }

    private void addControls() {
        storeAvatar = findViewById(R.id.storeAvatar);
        storeName = findViewById(R.id.storeName);
//        storeRating = findViewById(R.id.store_rating);
        storeAddress = findViewById(R.id.storeAddress);
        rvProducts = findViewById(R.id.rv_products);
    }

    private void updateStoreInfo(Store store) {
        storeName.setText(store.getName());
        storeAddress.setText(store.getFullAddress());
        Glide.with(StoreActivity.this).load(store.getAvatarUrl()).into(storeAvatar);
    }

    private void loadStoreInfo() {
        viewModel.loadStoreInfo(storeId);
    }

    private void loadProducts(String storeId) {
        productViewModel.getProducts(storeId);

        productViewModel.products.observe(this, products -> {
            Log.d("StoreActivity", "Products: " + products.size());
            productList.clear();
            productList.addAll(products);
            productAdapter.notifyDataSetChanged();
        });
    }
}