package com.example.agrimart.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public MutableLiveData<List<Product>> products = new MutableLiveData<>();
    public MutableLiveData<String> sortOrder = new MutableLiveData<>();


    public void setSortOrder(String sortOrder) {
        this.sortOrder.setValue(sortOrder);  // Đảm bảo việc thay đổi giá trị trong ViewModel
    }

    public void getProductsByCategory(String categoryID) {
        db.collection("products")
                .whereEqualTo("category_id", categoryID)
                .whereEqualTo("status","available")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> productList = task.getResult().toObjects(Product.class);
                        products.setValue(productList);
                        applySort();
                    } else {
                        Log.e("CategoryViewModel", "Error getting products: ", task.getException());
                    }
                });
    }

    public void applySort() {
        if (products.getValue() != null && sortOrder.getValue() != null) {
            List<Product> sortedList = new ArrayList<>(products.getValue());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            switch (sortOrder.getValue()) {
                case "lowest_price":
                    Collections.sort(sortedList, Comparator.comparing(Product::getPrice));
                    Log.d("CategoryViewModel", "Sorted by lowest_price");
                    break;

                case "highest_price":
                    Collections.sort(sortedList, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                    Log.d("CategoryViewModel", "Sorted by highest_price");
                    break;

                case "latest":
                    Collections.sort(sortedList, (a, b) -> {
                        try {
                            Date dateA = dateFormat.parse(a.getCreated_at());
                            Date dateB = dateFormat.parse(b.getCreated_at());
                            return dateB.compareTo(dateA); // Latest first
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });
                    Log.d("CategoryViewModel", "Sorted by latest");
                    break;

                case "oldest":
                    Collections.sort(sortedList, (a, b) -> {
                        try {
                            Date dateA = dateFormat.parse(a.getCreated_at());
                            Date dateB = dateFormat.parse(b.getCreated_at());
                            return dateA.compareTo(dateB); // Oldest first
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });
                    Log.d("CategoryViewModel", "Sorted by oldest");
                    break;
            }
            products.setValue(sortedList);
        }
    }

}

