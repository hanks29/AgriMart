package com.example.agrimart.ui.PostProduct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.databinding.ActivityProductPreviewBinding;
import com.example.agrimart.viewmodel.ProductReviewViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProductPreviewActivity extends AppCompatActivity {

    private ActivityProductPreviewBinding binding;
    private ProductRequest product;
    private ProductReviewViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityProductPreviewBinding.inflate(getLayoutInflater());
        viewModel=new ViewModelProvider(this).get(ProductReviewViewModel.class);
        binding.setViewModel(viewModel);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        if(intent!=null)
        {
            product = (ProductRequest) intent.getSerializableExtra("product");

            viewModel.setProduct(product);
            viewModel.setCategory(intent.getStringExtra("category"));

            FirebaseFirestore db = FirebaseFirestore.getInstance();


            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null) {
                db.collection("users")
                        .whereEqualTo("email", user.getEmail())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    Log.d("KHANHHI", "onCreate: " + task.getResult().getDocuments().get(0).getString("store_name"));
                                    String image=task.getResult().getDocuments().get(0).getString("store_avatar");
                                    if(!Objects.isNull(image) && !image.equals(""))
                                    {
                                        Glide.with(this).load(image).into(binding.imageView4);
                                    }
                                }
                                else {
                                    Log.d("KHANHHI", "onCreate: khong co du lieu");
                                }
                            }
                        });
            }


        }

    }
}