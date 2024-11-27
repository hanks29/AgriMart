package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Store;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopRatingViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Map<String, Object>>> ratingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private String userId;
    private MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();

    public LiveData<List<Product>> getProductsLiveData() {
        return productsLiveData;
    }


    public ShopRatingViewModel(){
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void fetchProductRatings(String product_id, String productImg, String productName) {
        userId = auth.getCurrentUser().getUid();
        firestore.collection("rating")
                .whereEqualTo("product_id", product_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot snapshot = task.getResult();
                        List<Map<String, Object>> ratingsList = new ArrayList<>();
                        List<com.google.android.gms.tasks.Task<?>> userFetchTasks = new ArrayList<>();

                        // Iterate through each document in the snapshot
                        for (QueryDocumentSnapshot document : snapshot) {
                            String storedProductId = document.getString("product_id");
                            List<Map<String, Object>> ratings = (List<Map<String, Object>>) document.get("ratings");


                            // Check if the document belongs to the correct product and contains ratings
                            if (storedProductId != null && storedProductId.equals(product_id) && ratings != null) {
                                for (Map<String, Object> rating : ratings) {
                                    Map<String, Object> ratingMap = new HashMap<>();
                                    String uId = (String) rating.get("userId");
                                    assert uId != null;
                                    if(uId.equals(userId))
                                    {
                                        ratingMap.put("userId", userId);
                                        ratingMap.put("rating", rating.get("rating"));
                                        ratingMap.put("review", rating.get("review"));
                                        ratingMap.put("updatedAt", rating.get("updatedAt"));
                                        ratingMap.put("productImg", productImg);
                                        ratingMap.put("productName", productName);

                                        // Fetch user data if userId is present
                                        if (userId != null) {
                                            // Create Task to fetch user info
                                            com.google.android.gms.tasks.Task<DocumentSnapshot> userTask = firestore.collection("users")
                                                    .document(userId)
                                                    .get()
                                                    .addOnCompleteListener(userTaskSnapshot -> {
                                                        if (userTaskSnapshot.isSuccessful() && userTaskSnapshot.getResult() != null) {
                                                            DocumentSnapshot userDocument = userTaskSnapshot.getResult();
                                                            ratingMap.put("userImage", userDocument.getString("userImage"));
                                                            ratingMap.put("fullName", userDocument.getString("fullName"));
                                                        } else {
                                                            ratingMap.put("userImage", null);
                                                            ratingMap.put("fullName", "Unknown User");
                                                        }
                                                    });
                                            userFetchTasks.add(userTask);
                                        }

                                        ratingsList.add(ratingMap);
                                    }



                                }

                                // Add quantity and average rating to the result

                            }
                        }

                        // Wait for all user data fetch tasks to complete
                        Tasks.whenAllComplete(userFetchTasks).addOnCompleteListener(allTasks -> {
                            // Check if any tasks failed
                            boolean hasErrors = false;
                            for (com.google.android.gms.tasks.Task<?> userTask : userFetchTasks) {
                                if (!userTask.isSuccessful()) {
                                    hasErrors = true;
                                    break;
                                }
                            }

                            // Set error message if any user data fetch failed
                            if (hasErrors) {
                                errorLiveData.setValue("Failed to fetch some user data.");
                            }

                            // Sort the ratings list by updatedAt
                            Collections.sort(ratingsList, (o1, o2) -> {
                                Object date1 = o1.get("updatedAt");
                                Object date2 = o2.get("updatedAt");

                                if (date1 instanceof com.google.firebase.Timestamp && date2 instanceof com.google.firebase.Timestamp) {
                                    Date time1 = ((com.google.firebase.Timestamp) date1).toDate();
                                    Date time2 = ((com.google.firebase.Timestamp) date2).toDate();
                                    return time2.compareTo(time1); // Sort in descending order
                                }
                                return 0;
                            });

                            // Update LiveData with the final ratings list
                            ratingsLiveData.setValue(ratingsList);
                        });
                    } else {
                        // Handle errors when fetching data
                        if (task.getException() != null) {
                            errorLiveData.setValue(task.getException().getMessage());
                        } else {
                            errorLiveData.setValue("Failed to fetch data.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure in Firestore query
                    errorLiveData.setValue(e.getMessage());
                    Log.e("FirestoreError", "Error fetching ratings", e);
                });
    }

    public void getProduct(String storeId, OnProductsFetchedCallback callback) {
        firestore.collection("products")
                .whereEqualTo("storeId", storeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Product> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                products.add(product);
                            }
                        }
                        // Trả kết quả qua callback
                        callback.onSuccess(products);
                    } else {
                        Log.e("ShopRatingViewModel", "Failed to fetch products: " + task.getException());
                        callback.onFailure(task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ShopRatingViewModel", "Error fetching products", e);
                    callback.onFailure(e);
                });
    }

    // Interface callback
    public interface OnProductsFetchedCallback {
        void onSuccess(List<Product> products);
        void onFailure(Exception e);
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
