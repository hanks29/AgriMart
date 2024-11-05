package com.example.agrimart.ui.ProductPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Store;
import com.example.agrimart.ui.Store.StoreActivity;
import com.example.agrimart.viewmodel.ProductDetailViewModel;


import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private ImageButton btn_back, btnExpand;
    private TextView description, productName, productPrice, storeName, storeAddress, storePhoneNumber;
    private ImageView storeAvatar;
    private boolean isExpanded = false;
    private LinearLayout store;
    private ProductDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        description = findViewById(R.id.description);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        btnExpand = findViewById(R.id.btn_expand);
        btn_back = findViewById(R.id.btn_back);
        store = findViewById(R.id.store);
        storeName = findViewById(R.id.store_name);
        storeAddress = findViewById(R.id.address);
        storeAvatar = findViewById(R.id.store_img);

        btn_back.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        Product product = (Product) intent.getSerializableExtra("product");

        if (product != null) {
            productName.setText(product.getName());
            productPrice.setText(String.format("%,.0f đ", product.getPrice()));
            description.setText(product.getDescription());

            ArrayList<SlideModel> imgList = new ArrayList<>();
            if (product.getImages() != null) {
                for (String url : product.getImages()) {
                    imgList.add(new SlideModel(url, "", ScaleTypes.FIT));
                }
            }
            ImageSlider imageSlider = findViewById(R.id.product_image);
            imageSlider.setImageList(imgList, ScaleTypes.FIT);

            viewModel.loadStoreInfo(product.getStoreId());
        }

        viewModel.getStoreInfo().observe(this, new Observer<Store>() {
            @Override
            public void onChanged(Store storeInfo) {
                if (storeInfo != null) {
                    storeName.setText(storeInfo.getName());
                    storeAddress.setText(storeInfo.getFullAddress());
                    Glide.with(ProductDetailActivity.this).load(storeInfo.getAvatarUrl()).into(storeAvatar);
                }
            }
        });

        btnExpand.setOnClickListener(v -> {
            if (isExpanded) {
                description.setMaxLines(3);
                btnExpand.setImageResource(R.drawable.down);
            } else {
                description.setMaxLines(Integer.MAX_VALUE);
                btnExpand.setImageResource(R.drawable.arrowhead_up);
            }
            isExpanded = !isExpanded;
        });

        store.setOnClickListener(v -> {
            Intent storeIntent = new Intent(ProductDetailActivity.this, StoreActivity.class);
            storeIntent.putExtra("storeId", product.getStoreId());
            startActivity(storeIntent);
        });
    }
}
