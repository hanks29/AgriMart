package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Category;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeFragmentViewModel extends ViewModel {
    public MutableLiveData<List<Category>> categories;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public HomeFragmentViewModel() {
        categories = new MutableLiveData<>();
    }

    public void getData(){
        db.collection("categories")
                .orderBy("id")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Category> categoryList = task.getResult().toObjects(Category.class);
                categories.setValue(categoryList);
            } else {
                Log.e("HomeFragmentViewModel", "Error getting documents: ", task.getException());
            }
        });
    }
}