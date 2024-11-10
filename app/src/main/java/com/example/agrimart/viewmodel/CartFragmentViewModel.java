package com.example.agrimart.viewmodel;

import com.example.agrimart.data.model.StoreCart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartFragmentViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    // Constructor
    public CartFragmentViewModel() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    // Hàm để lấy danh sách các StoreCart theo userId
    public void getStoreCartsByUserId( final OnDataFetchedListener listener) {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("cart")
                .whereEqualTo("userId", userId) // Tìm các StoreCart có userId trùng với tham số
                .get() // Lấy dữ liệu
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<StoreCart> storeCartList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            StoreCart storeCart = document.toObject(StoreCart.class);
                            if (storeCart != null) {
                                storeCartList.add(storeCart);
                            }
                        }
                        // Gọi callback để trả về dữ liệu
                        listener.onDataFetched(storeCartList);
                    } else {
                        listener.onError("Failed to fetch data: " + task.getException().getMessage());
                    }
                });
    }

    // Listener để callback khi lấy dữ liệu xong
    public interface OnDataFetchedListener {
        void onDataFetched(List<StoreCart> storeCarts);
        void onError(String errorMessage);
    }
}
