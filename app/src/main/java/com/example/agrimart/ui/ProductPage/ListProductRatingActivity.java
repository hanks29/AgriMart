package com.example.agrimart.ui.ProductPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.RatingAdapter;
import com.example.agrimart.data.model.Rating;
import com.example.agrimart.ui.MainActivity;
import com.example.agrimart.viewmodel.ProductRatingFragmentViewModel;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import per.wsj.library.AndRatingBar;

public class ListProductRatingActivity extends AppCompatActivity {

    private RecyclerView ratingRecyclerView;
    private RatingAdapter ratingAdapter;
    private List<Rating> ratingList = new ArrayList<>();
    private String productId;
    private ProductRatingFragmentViewModel productRatingFragmentViewModel;
    private TextView numberReviews, numberRating;
    private AndRatingBar rating;
    private ImageButton btnBack, btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_product_rating);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();

        productId = getIntent().getStringExtra("productId");

        productRatingFragmentViewModel.getRatingsLiveData().observe(this, new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> ratingsData) {
                if (ratingsData != null) {
                    // Chuyển đổi danh sách Map thành danh sách Rating
                    ratingList.clear();

                    Integer quantity = 0;
                    Double averageRating = 0.0;

                    if (!ratingsData.isEmpty()) {
                        Map<String, Object> firstItem = ratingsData.get(0);

                        // Sử dụng cast an toàn với kiểm tra null
                        if (firstItem.get("quantity") != null) {
                            quantity = (Integer) firstItem.get("quantity");
                        }

                        if (firstItem.get("rating") != null) {
                            averageRating = ((Double) firstItem.get("rating"));
                        }
                    }

                    numberReviews.setText("("+quantity+")");
                    numberRating.setText(String.format("%.1f", averageRating));
                    rating.setRating((float) (averageRating*1.0));



                    for (int i = 1; i < ratingsData.size(); i++) {
                        Map<String, Object> data = ratingsData.get(i);
                        String userId = (String) data.get("userId");

                        Rating rating = new Rating(
                                userId,
                                (String) data.get("rating"),
                                (String) data.get("review"),
                                (Timestamp) data.get("updatedAt"),
                                (String) data.get("userImage"),
                                (String) data.get("fullName")
                        );

                        ratingList.add(rating);
                    }


                    // Cập nhật lại adapter
                    ratingAdapter.notifyDataSetChanged();
                }
            }
        });

        // Fetch dữ liệu đánh giá từ Firestore
        productRatingFragmentViewModel.fetchProductRatings(productId);

        // Khởi tạo RatingAdapter với danh sách Rating
        ratingAdapter = new RatingAdapter(this, ratingList, productRatingFragmentViewModel);

        // Thiết lập RecyclerView với LayoutManager và Adapter
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingRecyclerView.setAdapter(ratingAdapter);


        addEvent();
    }

    void addControl()
    {
        ratingRecyclerView = findViewById(R.id.ratingProduct);
        numberRating = findViewById(R.id.numberRating);
        numberReviews = findViewById(R.id.numberReviews);
        rating = findViewById(R.id.rating);
        btnBack = findViewById(R.id.btn_back);
        btnCart = findViewById(R.id.btn_cart);

        productRatingFragmentViewModel = new ProductRatingFragmentViewModel();
    }

    void addEvent()
    {
        btnBack.setOnClickListener(v -> finish());

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Intent to open MainActivity
                Intent intent = new Intent(ListProductRatingActivity.this, MainActivity.class);
                intent.putExtra("selected_item_id", R.id.cart);
                startActivity(intent);
            }
        });
    }
}