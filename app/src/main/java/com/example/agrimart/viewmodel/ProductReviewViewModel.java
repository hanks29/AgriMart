package com.example.agrimart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.data.model.ProductResponse;

import java.util.List;

public class ProductReviewViewModel extends ViewModel {
    private MutableLiveData<ProductResponse> products;

    public LiveData<String> price;
    public LiveData<String> category;
    public LiveData<String> quantity;
    public LiveData<String> description;
    public LiveData<String> nameStore;
    public LiveData<String> name;


    public ProductReviewViewModel() {
        this.products = new MutableLiveData<>();
    }

    public void setProduct(ProductResponse product) {
        products.setValue(product);
        price = Transformations.map(products, product1 ->
                product1 != null ? String.valueOf(product.getPrice()) : ""
        );

        quantity = Transformations.map(products, product1 ->
                product1 != null ? String.valueOf(product.getQuantity()) : ""
        );
        description = Transformations.map(products, product1 ->
                product1 != null ? product.getDescription() : ""
        );
        name = Transformations.map(products, product1 ->
                product1 != null ? product.getName() : ""
        );
    }
    public void setNameStore(String name_store) {
        this.nameStore = new MutableLiveData<>(name_store);
    }

    public void setCategory(String category) {
        this.category = new MutableLiveData<>(category);
    }






}
