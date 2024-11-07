package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public MutableLiveData<List<Product>> products = new MutableLiveData<>();

    public void getProductsByCategory(String categoryID) {
        db.collection("products")
                .whereEqualTo("category_id", categoryID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> productList = task.getResult().toObjects(Product.class);
                        products.setValue(productList);
                    } else {
                        Log.e("CategoryViewModel", "Error getting products: ", task.getException());
                    }
                });
    }
}

