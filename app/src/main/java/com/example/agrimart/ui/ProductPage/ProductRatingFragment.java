package com.example.agrimart.ui.ProductPage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.RatingAdapter;
import com.example.agrimart.data.model.Rating;
import com.example.agrimart.viewmodel.ProductRatingFragmentViewModel;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import per.wsj.library.AndRatingBar;

public class ProductRatingFragment extends Fragment {

    private RecyclerView ratingRecyclerView;
    private RatingAdapter ratingAdapter;
    private List<Rating> ratingList = new ArrayList<>();
    private String productId;
    private ProductRatingFragmentViewModel productRatingFragmentViewModel;
    private TextView numberReviews, numberRating, allRating;
    private AndRatingBar rating;
    private LinearLayout nextAllRating;

    public ProductRatingFragment() {

    }

    public ProductRatingFragment(String productId) {
        this.productId = productId;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_rating, container, false);

        // Ánh xạ RecyclerView
        ratingRecyclerView = view.findViewById(R.id.ratingProduct);
        numberRating = view.findViewById(R.id.numberRating);
        numberReviews = view.findViewById(R.id.numberReviews);
        rating = view.findViewById(R.id.rating);
        allRating = view.findViewById(R.id.allRating);
        nextAllRating = view.findViewById(R.id.nextAllRating);

        // Khởi tạo ViewModel
        productRatingFragmentViewModel = new ProductRatingFragmentViewModel();


        // Lắng nghe sự thay đổi của LiveData từ ViewModel
        productRatingFragmentViewModel.getRatingsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Map<String, Object>>>() {
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


                    // Giới hạn chỉ lấy 3 đánh giá đầu tiên
                    for (int i = 1; i < Math.min(ratingsData.size(), 4); i++) {
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
        ratingAdapter = new RatingAdapter(requireContext(), ratingList, productRatingFragmentViewModel);

        // Thiết lập RecyclerView với LayoutManager và Adapter
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ratingRecyclerView.setAdapter(ratingAdapter);

        allRating.setOnClickListener(v -> nextAllRating());
        nextAllRating.setOnClickListener(v -> nextAllRating());

        return view;
    }

    void nextAllRating() {
        // Create an Intent to start ProductRatingActivity
        Intent intent = new Intent(requireActivity(), ListProductRatingActivity.class);

        // Pass the productId as an extra to the intent
        intent.putExtra("productId", productId);

        // Start the activity
        startActivity(intent);
    }

}
