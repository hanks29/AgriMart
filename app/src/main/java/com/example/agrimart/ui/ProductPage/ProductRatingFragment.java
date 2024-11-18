package com.example.agrimart.ui.ProductPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductRatingFragment extends Fragment {

    private RecyclerView ratingRecyclerView;
    private RatingAdapter ratingAdapter;
    private List<Rating> ratingList = new ArrayList<>();
    private String productId;
    private ProductRatingFragmentViewModel productRatingFragmentViewModel;

    public ProductRatingFragment(String productId) {
        this.productId = productId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_rating, container, false);

        // Ánh xạ RecyclerView
        ratingRecyclerView = view.findViewById(R.id.ratingProduct);

        // Khởi tạo ViewModel
        productRatingFragmentViewModel = new ProductRatingFragmentViewModel();

        // Lắng nghe sự thay đổi của LiveData từ ViewModel
        productRatingFragmentViewModel.getRatingsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> ratingsData) {
                if (ratingsData != null) {
                    // Chuyển đổi danh sách Map thành danh sách Rating
                    ratingList.clear();
                    for (Map<String, Object> data : ratingsData) {
                        Timestamp timestamp = (Timestamp) data.get("updatedAt");
                        String updatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timestamp.toDate());

                        Rating rating = new Rating(
                                (String) data.get("userId"),
                                (String) data.get("rating"),
                                (String) data.get("review"),
                                updatedAt
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

        return view;
    }
}