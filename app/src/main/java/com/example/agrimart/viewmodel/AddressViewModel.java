package com.example.agrimart.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.agrimart.data.model.Address;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import java.util.Map;

public class AddressViewModel extends AndroidViewModel {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String userId;

    private MutableLiveData<String> addressUploadStatus = new MutableLiveData<>();

    public AddressViewModel(Application application) {
        super(application);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
    }

    public LiveData<String> getAddressUploadStatus() {
        return addressUploadStatus;
    }

    public void uploadAddress(Address newAddress, boolean isDefault, List<Map<String, Object>> addressMaps) {
        // Check if this is the first address or default address
        boolean isFirstAddress = (addressMaps == null || addressMaps.isEmpty());

        if (isFirstAddress || isDefault) {
            newAddress.setDefault(true);
            updateAddressesToNotDefault(newAddress);
        } else {
            addAddress(newAddress);
        }
    }

    private void addAddress(Address newAddress) {
        firestore.collection("users").document(userId)
                .update("addresses", FieldValue.arrayUnion(newAddress))
                .addOnSuccessListener(aVoid -> addressUploadStatus.setValue("Address added successfully"))
                .addOnFailureListener(e -> addressUploadStatus.setValue("Error: " + e.getMessage()));
    }

    private void updateAddressesToNotDefault(Address newAddress) {
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.contains("addresses")) {
                            List<Map<String, Object>> addressMaps = (List<Map<String, Object>>) document.get("addresses");
                            // Process to update addresses
                        }
                    }
                });
    }
}
