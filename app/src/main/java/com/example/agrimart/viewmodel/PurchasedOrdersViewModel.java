package com.example.agrimart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class PurchasedOrdersViewModel {

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private String userId;
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public PurchasedOrdersViewModel() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }



    public void getData(String status) {
        userId = auth.getCurrentUser().getUid();

        firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .orderBy("created_at")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Order> orderList = task.getResult().toObjects(Order.class);
                        orders.setValue(orderList);
                    } else {

                    }
                });
    }

}
