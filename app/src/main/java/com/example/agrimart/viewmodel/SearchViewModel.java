package com.example.agrimart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private String inputText;
    private MutableLiveData<List<Category>> categories;
    private MutableLiveData<List<Product>> products=new MutableLiveData<>();

    public void setCategories(List<Category> categories) {
        this.categories.setValue(categories);
    }

    public void setProducts(List<Product> products) {
        this.products.setValue(products);
    }

    public LiveData<Boolean> isProductsEmpty(){
        return Transformations.map(products, List::isEmpty);
    }
}
