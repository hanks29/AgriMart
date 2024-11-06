package com.example.agrimart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Product>> getProductsByCategory(String categoryId) {
        db.collection("products")
                .whereEqualTo("category_id", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
                    products.setValue(productList);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
        return products;
    }
}