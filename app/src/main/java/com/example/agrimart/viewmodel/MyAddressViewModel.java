package com.example.agrimart.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.agrimart.data.model.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAddressViewModel extends ViewModel {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Address>> addresses = new MutableLiveData<>();
    private final MutableLiveData<String> userId = new MutableLiveData<>();

    public MyAddressViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId.setValue(currentUser.getUid());
            loadAddresses();
        } else {
            Log.d("MyAddressViewModel", "User not logged in");
        }
    }

    public LiveData<List<Address>> getAddresses() {
        return addresses;
    }

    public void loadAddresses() {
        String uid = userId.getValue();
        if (uid == null) return;

        firestore.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists() && document.contains("addresses")) {
                            List<Map<String, Object>> addressMaps = (List<Map<String, Object>>) document.get("addresses");
                            updateAddressList(addressMaps);
                        }
                    } else {
                        Log.e("MyAddressViewModel", "Failed to load addresses", task.getException());
                    }
                });
    }

    private void updateAddressList(List<Map<String, Object>> addressMaps) {
        List<Address> defaultAddresses = new ArrayList<>();
        List<Address> nonDefaultAddresses = new ArrayList<>();

        for (Map<String, Object> addressMap : addressMaps) {
            Address address = new Address();
            address.setAddressId((String) addressMap.get("addressId"));
            address.setStreet((String) addressMap.get("street"));
            address.setCommune((String) addressMap.get("commune"));
            address.setProvince((String) addressMap.get("province"));
            address.setDistrict((String) addressMap.get("district"));
            address.setPhone((String) addressMap.get("phone"));
            address.setName((String) addressMap.get("name"));
            address.setDefault((Boolean) addressMap.get("default"));

            if (address.isDefault()) {
                defaultAddresses.add(address);
            } else {
                nonDefaultAddresses.add(address);
            }
        }

        List<Address> combinedList = new ArrayList<>();
        combinedList.addAll(defaultAddresses);
        combinedList.addAll(nonDefaultAddresses);
        addresses.setValue(combinedList);
    }
}
