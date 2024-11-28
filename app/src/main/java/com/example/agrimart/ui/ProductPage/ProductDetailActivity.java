package com.example.agrimart.ui.ProductPage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.example.agrimart.ui.Cart.CheckoutActivity;
import com.example.agrimart.ui.MainActivity;
import com.example.agrimart.ui.Store.StoreActivity;
import com.example.agrimart.viewmodel.CartFragmentViewModel;
import com.example.agrimart.viewmodel.ProductDetailViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private ImageButton btn_back, btnExpand, btnCart;
    private TextView description, productName, productPrice, storeName, storeAddress, storePhoneNumber;
    private ImageView storeAvatar;
    private boolean isExpanded = false;
    private LinearLayout store;
    private ProductDetailViewModel viewModel;
    private Button add_cart, buy_now;
    private Product product;
    private CartFragmentViewModel cartFragmentViewModel;
    LinearLayout reviewsLayout;

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
        add_cart = findViewById(R.id.add_cart);
        buy_now = findViewById(R.id.buy_now);
        btnCart = findViewById(R.id.btn_cart);
        reviewsLayout = findViewById(R.id.reviews);


        btn_back.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");

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

        cartFragmentViewModel = new ViewModelProvider(this).get(CartFragmentViewModel.class);
        add_cart.setOnClickListener(v -> showBottomSheetDialog(false));

        buy_now.setOnClickListener(v -> showBottomSheetDialog(true));

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to open MainActivity
                Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                intent.putExtra("selected_item_id", R.id.cart);
                startActivity(intent);
            }
        });


        ProductRatingFragment productRatingFragment = new ProductRatingFragment(product.getProduct_id());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reviews, productRatingFragment)
                .commit();
    }

    private void showBottomSheetDialog(boolean isBuyNow) {


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);

        Button addToCart = bottomSheetView.findViewById(R.id.continue_shopping);
        ImageView img_product = bottomSheetView.findViewById(R.id.img_product);
        TextView tvPrice = bottomSheetView.findViewById(R.id.tvPrice);
        TextView quantity = bottomSheetView.findViewById(R.id.quantity);
        ImageView btn_decrease = bottomSheetView.findViewById(R.id.btn_decrease);
        TextView warehouse = bottomSheetView.findViewById(R.id.warehouse);
        ImageView btn_increase = bottomSheetView.findViewById(R.id.btn_increase);

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(this)
                    .load(product.getImages().get(0))
                    .into(img_product);
        }

        if (isBuyNow) {
            addToCart.setText("Mua ngay");
            // Sử dụng Color.parseColor() để chuyển chuỗi màu hex thành màu hợp lệ
            addToCart.setBackgroundColor(Color.parseColor("#F2EE9A00"));
        }


        tvPrice.setText(productPrice.getText());
        warehouse.setText("Kho: " + product.getQuantity());

        AtomicInteger dem = new AtomicInteger(1);
        quantity.setText(String.valueOf(dem.get()));

        btn_decrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantity.getText().toString());
            if (currentQuantity > 1) {
                dem.getAndDecrement();
                quantity.setText(String.valueOf(dem.get()));
            }
        });

        btn_increase.setOnClickListener(v -> {
            if (dem.get() < product.getQuantity()) {
                dem.getAndIncrement();
                quantity.setText(String.valueOf(dem.get()));
            }
        });

         addToCart.setOnClickListener(v -> {
            if (product != null) {
                int q = Integer.parseInt(quantity.getText().toString());
                if (isBuyNow) {
                    Intent checkoutIntent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                    ArrayList<Product> selectedProducts = new ArrayList<>();
                    product.setQuantity(q);
                    selectedProducts.add(product);
                    checkoutIntent.putParcelableArrayListExtra("selectedProducts", selectedProducts);
                    checkoutIntent.putExtra("storeName", storeName.getText().toString());
                    startActivity(checkoutIntent);
                } else {
                    cartFragmentViewModel.addProductToCart(product.getStoreId(), product, q);
                    Toast.makeText(this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}
