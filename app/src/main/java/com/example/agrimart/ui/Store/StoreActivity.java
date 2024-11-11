package com.example.agrimart.ui.Store;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.agrimart.adapter.ProductManagerAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Store;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.HomeFragmentViewModel;
import com.example.agrimart.viewmodel.ProductDetailViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoreActivity extends AppCompatActivity {

    private ImageView storeAvatar;
    private TextView storeName, storeRating, storeAddress;
    private RecyclerView rvProducts;
    private ProductManagerAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private String storeId;
    private ProductDetailViewModel viewModel;
    private HomeFragmentViewModel productViewModel;
    private SharedPreferences sharedPreferences;
    private Button btnEditPro,btnDeletePro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        setupWindowInsets();
        addControls();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        storeId = sharedPreferences.getString("user_id", "");
        Log.d("StoreActivity123", "Store ID: " + storeId);
        viewModel = new ProductDetailViewModel();
        productViewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        productList = new ArrayList<>();
        productAdapter = new ProductManagerAdapter(productList);

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);

        viewModel.getStoreInfo().observe(this, store -> {
            if (store != null) {
                updateStoreInfo(store);
            }
        });

        loadStoreInfo();
        loadProducts(storeId);

        btnEditPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEditPro.getText().equals("Cancel")){
                    btnEditPro.setText("Edit");
                    btnDeletePro.setVisibility(View.GONE);
                    productAdapter.setEditMode(false);
                    productAdapter.selectedProducts.clear();
                } else {
                    btnEditPro.setText("Cancel");
                    btnDeletePro.setVisibility(View.VISIBLE);
                    productAdapter.setEditMode(true);
                }
            }
        });
        btnDeletePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(productAdapter.selectedProducts.size()<1){
                    Toast.makeText(StoreActivity.this, "Chọn sản phẩm cần xóa", Toast.LENGTH_SHORT).show();
                }else{
                    deleteProducts();
                }

            }
        });
    }

    private void deleteProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        productAdapter.setEditMode(false);
        for (Product product : productAdapter.selectedProducts) {
            DocumentReference productRef=db.collection("products").document(product.getProductId());

            productRef.update("status","delete")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(StoreActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            int productIndex = productList.indexOf(product);
                            if (productIndex != -1) {
                                productList.remove(productIndex);
                                productAdapter.selectedProducts.remove(product);
                                productAdapter.notifyItemRemoved(productIndex);
                            }


                            if (productAdapter.selectedProducts.isEmpty()) {
                                btnEditPro.setText("Edit");
                                btnDeletePro.setVisibility(View.GONE);
                            }

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(StoreActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    });
        }



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
        storeRating = findViewById(R.id.store_rating);
        storeAddress = findViewById(R.id.storeAddress);
        rvProducts = findViewById(R.id.rv_products);
        btnEditPro = findViewById(R.id.btnEdit);
        btnDeletePro=findViewById(R.id.btn_delete);
    }

    private void updateStoreInfo(Store store) {
        storeName.setText(store.getName());
        storeAddress.setText(store.getFullAddress());
        storeRating.setText("Đánh giá: 4.5");
        Glide.with(StoreActivity.this).load(store.getAvatarUrl()).into(storeAvatar);
    }

    private void loadStoreInfo() {
        viewModel.loadStoreInfo(storeId);
    }

    private void loadProducts(String storeId) {
        productViewModel.getProducts(storeId);

        productViewModel.products.observe(this, products -> {
            Log.d("StoreActivity123", "Products: " + products.size());
            productList.clear();
            productList.addAll(products);
            productAdapter.notifyDataSetChanged();
        });
    }
}