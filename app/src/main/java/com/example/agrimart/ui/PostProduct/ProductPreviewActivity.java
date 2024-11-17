package com.example.agrimart.ui.PostProduct;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.data.model.ProductResponse;
import com.example.agrimart.databinding.ActivityProductPreviewBinding;
import com.example.agrimart.ui.MyProfile.MyStore.MyStoreActivity;
import com.example.agrimart.viewmodel.ProductReviewViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class ProductPreviewActivity extends AppCompatActivity {

    private ActivityProductPreviewBinding binding;
    private ProductResponse product;
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
            product = (ProductResponse) intent.getSerializableExtra("product");
            if (product == null) {
                product = new ProductResponse();
            }
            viewModel.setProduct(product);
            getCategoryFromFirebase(product.getCategory());
//            viewModel.setCategory(product.getCategory());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

        }
        binding.btnHome.setOnClickListener(view -> {
            Intent intent1 = new Intent(ProductPreviewActivity.this, MyStoreActivity.class);
            startActivity(intent1);
        });

        binding.editPro.setOnClickListener(view -> {
            Dialog dialog = new Dialog(ProductPreviewActivity.this);
            dialog.setContentView(R.layout.dialog_edit_product);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);
            EditText edtName = dialog.findViewById(R.id.edtName);
            EditText edtPrice = dialog.findViewById(R.id.edtPrice);
            EditText edtDes = dialog.findViewById(R.id.edtDescription);
            EditText edtQuantity = dialog.findViewById(R.id.edtQuantity);
            dialog.show();
            btnUpdate.setOnClickListener(view1 -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if(edtName.getText().toString().equals("") || edtPrice.getText().toString().equals("") || edtDes.getText().toString().equals("") || edtQuantity.getText().toString().equals(""))
                {
                    Toast.makeText(ProductPreviewActivity.this, "Please fill all the information", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.collection("products").document(product.getProductId())
                                .update("name", edtName.getText().toString(),
                                        "price", Integer.parseInt(edtPrice.getText().toString()),
                                        "quantity", Integer.parseInt(edtQuantity.getText().toString())+product.getQuantity(),
                                        "description", product.getDescription())
                                        .addOnSuccessListener(aVoid -> {
                                            binding.tvStockQuantity.setText("Còn "+Integer.parseInt(edtQuantity.getText().toString())+product.getQuantity());
                                            Toast.makeText(ProductPreviewActivity.this, "Update product successful", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ProductPreviewActivity.this, "Update product failed", Toast.LENGTH_SHORT).show();
                                        });
                dialog.dismiss();
            });
            btnCancel.setOnClickListener(view1 -> {
                dialog.dismiss();
            });
        });
    }

    private void getCategoryFromFirebase(String categoryId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .whereEqualTo("id", categoryId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Category> categories = task.getResult().toObjects(Category.class);
                            Log.d("KHANHHI", "onComplete: "+categories.get(0).getName());
                            viewModel.setCategory(categories.get(0).getName());
                            binding.tvCategory.setText(categories.get(0).getName());

                        }
                    }
                });
    }
}