package com.example.agrimart.ui.MyProfile.MyRating;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.RatingAdapter;
import com.example.agrimart.adapter.RatingShopAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Rating;
import com.example.agrimart.viewmodel.ShopRatingViewModel;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShopRatingActivity extends AppCompatActivity {
    private ImageView btnBack;
    private RecyclerView ratingRecyclerView;
    private ShopRatingViewModel shopRatingViewModel;
    private String storeId;
    private List<Rating> ratingList = new ArrayList<>();
    private RatingShopAdapter ratingAdapter;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_rating);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        shopRatingViewModel = new ViewModelProvider(this).get(ShopRatingViewModel.class);

        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            // Hiển thị thông báo lỗi hoặc thoát Activity
            finish(); // Đóng Activity nếu `order` null
            return;
        }
        storeId = order.getSellerId();

        shopRatingViewModel.getProduct(storeId, new ShopRatingViewModel.OnProductsFetchedCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                for (Product p : products)
                {
                    shopRatingViewModel.fetchProductRatings(p.getProduct_id(), p.getImages().get(0), p.getName());
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Hiển thị lỗi hoặc thông báo

            }
        });




        addControl();

        shopRatingViewModel.getRatingsLiveData().observe(this, new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> ratingsData) {
                if (ratingsData != null) {

                    for (Map<String, Object> data : ratingsData) {
                        Rating newRating = parseRating(data);
                        if (!ratingList.contains(newRating)) { // Kiểm tra để tránh trùng lặp
                            ratingList.add(newRating);
                        }
                    }

                    // Sắp xếp ratingList theo updatedAt (mới nhất lên đầu)
                    Collections.sort(ratingList, (r1, r2) -> r2.getUpdatedAt().compareTo(r1.getUpdatedAt()));

                    // Thông báo adapter cập nhật dữ liệu
                    ratingAdapter.notifyDataSetChanged();
                }
            }
        });





        ratingAdapter = new RatingShopAdapter(this, ratingList);

        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingRecyclerView.setAdapter(ratingAdapter);

        btnBack.setOnClickListener(v -> finish());
    }

    void addControl()
    {
        ratingRecyclerView = findViewById(R.id.ratingProduct);
        btnBack = findViewById(R.id.btn_back);
    }

    private Rating parseRating(Map<String, Object> data) {
        return new Rating(
                (String) data.get("userId"),
                (String) data.get("rating"),
                (String) data.get("review"),
                (Timestamp) data.get("updatedAt"),
                (String) data.get("userImage"),
                (String) data.get("fullName"),
                (String) data.get("productImg"),
                (String) data.get("productName")
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shopRatingViewModel.getRatingsLiveData().removeObservers(this);
    }


}