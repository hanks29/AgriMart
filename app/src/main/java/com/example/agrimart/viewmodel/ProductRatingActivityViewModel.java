package com.example.agrimart.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRatingActivityViewModel extends ViewModel {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private final String userId;

    public ProductRatingActivityViewModel() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.userId = auth.getCurrentUser().getUid();
    }

    public void saveProductRating(String productId, double newRating, String review, String oderId) {
        // Trỏ đến tài liệu trong collection rating
        DocumentReference productRef = firestore.collection("rating").document(productId);

        // Đọc dữ liệu hiện tại để cập nhật hoặc tạo mới
        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Nếu tài liệu tồn tại, cập nhật quantity và rating
                updateExistingRating(productRef, documentSnapshot, newRating, review);
                updateCheckRating(oderId);
            } else {
                // Nếu tài liệu không tồn tại, tạo mới
                createNewRating(productRef, newRating, review);
                updateCheckRating(oderId);
            }
        }).addOnFailureListener(e -> {
            System.err.println("Lỗi khi đọc dữ liệu sản phẩm: " + e.getMessage());
        });
    }

    private void updateCheckRating(String orderId) {
        // Lấy tham chiếu đến Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Cập nhật trường 'checkRating' thành true trong tài liệu với orderId
        db.collection("orders")
                .document(orderId)
                .update("checkRating", true)
                .addOnSuccessListener(aVoid -> {
                    // Thành công, hiển thị thông báo
                    System.out.println("Trường checkRating đã được cập nhật thành true.");
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu có
                    System.err.println("Lỗi khi cập nhật trường checkRating: " + e.getMessage());
                });
    }

    private void updateExistingRating(DocumentReference productRef,
                                      com.google.firebase.firestore.DocumentSnapshot documentSnapshot,
                                      double newRating, String review) {
        // Lấy quantity và rating hiện tại
        Long quantity = documentSnapshot.getLong("quantity");
        Double currentRating = documentSnapshot.getDouble("rating");

        // Đảm bảo các giá trị không null
        quantity = (quantity != null) ? quantity : 0;
        currentRating = (currentRating != null) ? currentRating : 0.0;

        // Tính toán giá trị mới
        long updatedQuantity = quantity + 1;
        double updatedRating = (currentRating * quantity + newRating) / updatedQuantity;

        // Cập nhật lại dữ liệu của tài liệu
        Map<String, Object> updatedProductData = new HashMap<>();
        updatedProductData.put("quantity", updatedQuantity);
        updatedProductData.put("rating", updatedRating);

        productRef.update(updatedProductData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Dữ liệu sản phẩm đã được cập nhật.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
                });

        // Lưu đánh giá vào tập hợp con ratings
        saveRatingToSubcollection(productRef, newRating, review);
    }

    private void createNewRating(DocumentReference productRef, double newRating, String review) {
        // Tạo dữ liệu mới cho tài liệu
        Map<String, Object> newProductData = new HashMap<>();
        newProductData.put("quantity", 1);
        newProductData.put("rating", newRating);

        // Thêm tài liệu mới vào collection rating
        productRef.set(newProductData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Tài liệu mới đã được tạo trong collection rating.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi khi tạo tài liệu mới: " + e.getMessage());
                });

        // Lưu đánh giá đầu tiên vào tập hợp con ratings
        saveRatingToSubcollection(productRef, newRating, review);
    }

    private void saveRatingToSubcollection(DocumentReference productRef, double rating, String review) {

        // Lấy userId từ Firebase Authentication (hoặc từ nguồn khác)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tạo dữ liệu đánh giá
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);  // Lưu rating dưới dạng double
        ratingData.put("review", review);
        ratingData.put("updatedAt", new Timestamp(new Date()));
        ratingData.put("userId", userId);

        // Lấy tài liệu hiện tại từ productRef
        productRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy mảng ratings hiện tại từ tài liệu (nếu có)
                        List<Map<String, Object>> ratings = (List<Map<String, Object>>) documentSnapshot.get("ratings");

                        // Nếu ratings không tồn tại (null), tạo một mảng mới
                        if (ratings == null) {
                            ratings = new ArrayList<>();
                        }

                        // Thêm đánh giá mới vào mảng ratings
                        ratings.add(ratingData);

                        // Cập nhật lại tài liệu với mảng ratings mới
                        productRef.update("ratings", ratings)
                                .addOnSuccessListener(aVoid -> {
                                    // Thành công, hiển thị thông báo
                                    System.out.println("Đánh giá đã được lưu.");
                                })
                                .addOnFailureListener(e -> {
                                    // Xử lý lỗi nếu có
                                    System.err.println("Lỗi khi lưu đánh giá: " + e.getMessage());
                                });
                    } else {
                        // Nếu tài liệu không tồn tại, tạo một tài liệu mới với rating
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("ratings", new ArrayList<>(List.of(ratingData))); // Tạo mảng ratings mới chứa đánh giá

                        // Cập nhật tài liệu với ratings mới
                        productRef.set(productData, SetOptions.merge()) // Merge nếu tài liệu đã tồn tại
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Tài liệu sản phẩm mới đã được tạo và đánh giá đã được lưu.");
                                })
                                .addOnFailureListener(e -> {
                                    System.err.println("Lỗi khi tạo tài liệu và lưu đánh giá: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lấy tài liệu
                    System.err.println("Lỗi khi lấy tài liệu: " + e.getMessage());
                });
    }


}
