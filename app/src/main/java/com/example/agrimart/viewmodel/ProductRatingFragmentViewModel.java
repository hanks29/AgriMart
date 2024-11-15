package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRatingFragmentViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Map<String, Object>>> ratingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private String userId;
    private final MutableLiveData<String> userImageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> fullNameLiveData = new MutableLiveData<>();

    public ProductRatingFragmentViewModel() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    // Phương thức để đọc đánh giá sản phẩm từ Firestore
    public void fetchProductRatings(String product_id) {
        // Tham chiếu đến collection "users" (or your actual collection name)
        firestore.collection("rating").
                whereEqualTo("product_id", product_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot snapshot = task.getResult();
                        List<Map<String, Object>> ratingsList = new ArrayList<>();

                        // Iterate over all user documents
                        for (QueryDocumentSnapshot document : snapshot) {
                            // Get the product_id and ratings list from each user document
                            String storedProductId = document.getString("product_id");
                            List<Map<String, Object>> ratings = (List<Map<String, Object>>) document.get("ratings");

                            // Check if the product_id matches
                            if (storedProductId != null && storedProductId.equals(product_id) && ratings != null) {
                                // If the product_id matches, iterate over the ratings list
                                for (Map<String, Object> rating : ratings) {
                                    Map<String, Object> ratingMap = new HashMap<>();
                                    ratingMap.put("userId", rating.get("userId"));
                                    ratingMap.put("rating", rating.get("rating"));
                                    ratingMap.put("review", rating.get("review"));
                                    ratingMap.put("updatedAt", rating.get("updatedAt"));

                                    // Add to the ratings list
                                    ratingsList.add(ratingMap);
                                }
                            }
                        }

                        // Set the fetched ratings data into LiveData
                        ratingsLiveData.setValue(ratingsList);
                    } else {
                        if (task.getException() != null) {
                            errorLiveData.setValue(task.getException().getMessage());
                        } else {
                            errorLiveData.setValue("Failed to fetch data.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    errorLiveData.setValue(e.getMessage());
                    Log.e("FirestoreError", "Error fetching ratings", e);
                });
    }


    public void fetchUserProfile(String userId) {
        if (userId == null) {
            errorLiveData.setValue("User ID is null.");
            return;
        }
        firestore.collection("users")  // Giả sử "users" là tên collection của bạn
                .document(userId)      // Dùng userId để lấy tài liệu
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userImage = document.getString("userImage");  // Lấy userImage
                            String fullName = document.getString("fullName");    // Lấy fullName

                            // Gán giá trị vào LiveData
                            userImageLiveData.setValue(userImage);
                            fullNameLiveData.setValue(fullName);
                        } else {
                            errorLiveData.setValue("Không tìm thấy người dùng với ID này.");
                        }
                    } else {
                        errorLiveData.setValue("Không thể lấy dữ liệu.");
                    }
                })
                .addOnFailureListener(e -> {
                    errorLiveData.setValue(e.getMessage());
                });
    }

    // LiveData để quan sát ảnh người dùng
    public LiveData<String> getUserImageLiveData() {
        return userImageLiveData;
    }

    // LiveData để quan sát tên đầy đủ
    public LiveData<String> getFullNameLiveData() {
        return fullNameLiveData;
    }

    // LiveData để quan sát danh sách đánh giá
    public LiveData<List<Map<String, Object>>> getRatingsLiveData() {
        return ratingsLiveData;
    }

    // LiveData để quan sát lỗi
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
