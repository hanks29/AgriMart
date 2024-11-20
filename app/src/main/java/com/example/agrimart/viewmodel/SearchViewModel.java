package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<List<Product>> filteredProducts = new MutableLiveData<>(new ArrayList<>());
    private List<Product> originalProductList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Product>> getFilteredProducts() {
        return filteredProducts;
    }

    public void searchProductsByName(String query) {
        db.collection("products")
                .whereEqualTo("status", "available")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> products = task.getResult().toObjects(Product.class);
                        List<Product> filteredList = new ArrayList<>();
                        List<Product> remainingList = new ArrayList<>();

                        for (Product product : products) {
                            if (product.getName() != null) {
                                String productName = product.getName().toLowerCase();
                                String lowerCaseQuery = query.toLowerCase();
                                if (productName.startsWith(lowerCaseQuery)) {
                                    filteredList.add(product);
                                } else if (productName.contains(lowerCaseQuery)) {
                                    remainingList.add(product);
                                }
                            }
                        }
                        filteredList.addAll(remainingList);
                        originalProductList = new ArrayList<>(filteredList);
                        filteredProducts.setValue(filteredList);
                    } else {
                        filteredProducts.setValue(new ArrayList<>());
                    }
                });
    }

    public void searchProductsByCategory(String categoryId) {
        db.collection("products")
                .whereEqualTo("category_id", categoryId)
                .whereEqualTo("status", "available")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> products = task.getResult().toObjects(Product.class);
                        List<Product> availableProducts = new ArrayList<>();
                        for (Product product : products) {
                            availableProducts.add(product);
                        }
                        originalProductList = new ArrayList<>(availableProducts);
                        filteredProducts.setValue(availableProducts);
                    } else {
                        filteredProducts.setValue(new ArrayList<>());
                    }
                });
    }

    public void searchProductsByStoreName(String storeName) {
        db.collection("users")
                .whereEqualTo("store_name", storeName)
                .get()
                .addOnCompleteListener(storeTask -> {
                    if (storeTask.isSuccessful() && !storeTask.getResult().isEmpty()) {
                        String storeId = storeTask.getResult().getDocuments().get(0).getId();
                        db.collection("products")
                                .whereEqualTo("storeId", storeId)
                                .whereEqualTo("status", "available")
                                .get()
                                .addOnCompleteListener(productTask -> {
                                    if (productTask.isSuccessful()) {
                                        List<Product> products = productTask.getResult().toObjects(Product.class);
                                        List<Product> availableProducts = new ArrayList<>();
                                        for (Product product : products) {
                                            availableProducts.add(product);
                                        }
                                        originalProductList = new ArrayList<>(availableProducts);
                                        filteredProducts.setValue(availableProducts);
                                    } else {
                                        filteredProducts.setValue(new ArrayList<>());
                                    }
                                });
                    } else {
                        filteredProducts.setValue(new ArrayList<>());
                    }
                });
    }

    public void sortProducts(String sortBy) {
        List<Product> currentProducts = filteredProducts.getValue() != null ? new ArrayList<>(filteredProducts.getValue()) : new ArrayList<>();

        switch (sortBy) {
            case "Liên quan nhất":
                currentProducts = new ArrayList<>(originalProductList);
                break;
            case "Mới nhất":
                Collections.sort(currentProducts, (p1, p2) -> p2.getCreated_at().compareTo(p1.getCreated_at()));
                break;
            case "Cũ nhất":
                Collections.sort(currentProducts, (p1, p2) -> p1.getCreated_at().compareTo(p2.getCreated_at()));
                break;
            case "Giá cao nhất":
                Collections.sort(currentProducts, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case "Giá thấp nhất":
                Collections.sort(currentProducts, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            default:
                break;
        }

        filteredProducts.setValue(currentProducts);
    }

}