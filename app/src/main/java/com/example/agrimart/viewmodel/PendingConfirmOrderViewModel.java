package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PendingConfirmOrderViewModel extends ViewModel{
    public MutableLiveData<List<Order>> orderList ;
    public MutableLiveData<List<Order>> orderListApproved ;
    public MutableLiveData<List<Order>> orderListPicked ;
    public MutableLiveData<List<Order>> orderListDelivered ;
    public MutableLiveData<List<Order>> orderListCancel ;
    private FirebaseFirestore db ;

    public PendingConfirmOrderViewModel() {
        this.orderList = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
        this.orderListApproved = new MutableLiveData<>();
        this.orderListPicked = new MutableLiveData<>();
        this.orderListDelivered = new MutableLiveData<>();
        this.orderListCancel = new MutableLiveData<>();
    }

    public void getOrderListFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("orders")
                .whereEqualTo("status", "pending")
                .whereEqualTo("storeId", user.getUid())
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

    public void getOrderWithStatusApproved(){
        db.collection("orders")
                .whereEqualTo("status", "approved")
                .whereEqualTo("storeId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                        orderListApproved.setValue(orders);
                    }
                })
                .addOnFailureListener(e -> {
                    orderListApproved.setValue(null);
                });
    }

    public void getOrderWithStatusDelivering(){
        db.collection("orders")
                .whereEqualTo("status", "delivering")
                .whereEqualTo("storeId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                        orderListPicked.setValue(orders);
                        Log.d("PrintOrderAdapter111", "onSuccess: "+orders.size());
                    }
                })
                .addOnFailureListener(e -> {
                    orderListPicked.setValue(null);
                });
    }

    public void getDeliveredOrders(){
        db.collection("orders")
                .whereEqualTo("status", "delivered")
                .whereEqualTo("storeId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                        orderListDelivered.setValue(orders);
                        Log.d("PrintOrderAdapter111", "onSuccess: "+orders.size());
                    }
                })
                .addOnFailureListener(e -> {
                    orderListDelivered.setValue(null);
                });
    }

    public void getCancelOrders(){
        db.collection("orders")
                .whereEqualTo("status", "canceled")
                .whereEqualTo("storeId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                        orderListCancel.setValue(orders);
                        Log.d("PrintOrderAdapter111", "onSuccess: "+orders.size());
                    }
                })
                .addOnFailureListener(e -> {
                    orderListCancel.setValue(null);
                });
    }
}
