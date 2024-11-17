package com.example.agrimart.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PendingConfirmOrderViewHolder extends ViewModel{
    public MutableLiveData<List<Order>> orderList ;
    private FirebaseFirestore db ;

    public PendingConfirmOrderViewHolder() {
        this.orderList = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
    }

    public void getOrderListFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("orders")
                .whereEqualTo("sellerId", user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                        orderList.setValue(orders);
                    }
                })
                .addOnFailureListener(e -> {
                    orderList.setValue(null);
                });
    }
}
