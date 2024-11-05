package com.example.agrimart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Store;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ProductDetailViewModel extends ViewModel {
    private final FirebaseFirestore db;
    private ListenerRegistration storeListener;
    private final MutableLiveData<Store> storeInfo = new MutableLiveData<>();

    public ProductDetailViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<Store> getStoreInfo() {
        return storeInfo;
    }

    public void loadStoreInfo(String storeId) {
        if (storeListener != null) {
            storeListener.remove();
        }

        storeListener = db.collection("users").document(storeId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null || !snapshot.exists()) {
                        return;
                    }
                    String name = snapshot.getString("store_name");
                    String avatarUrl = snapshot.getString("store_avatar");
                    String street = snapshot.getString("store_address.street");
                    String ward = snapshot.getString("store_address.ward");
                    String district = snapshot.getString("store_address.district");
                    String city = snapshot.getString("store_address.city");
                    String phoneNumber = snapshot.getString("store_phone_number");

                    Store info = new Store(name, avatarUrl, street, ward, district, city, phoneNumber);
                    storeInfo.setValue(info);
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (storeListener != null) {
            storeListener.remove();
        }
    }

}
