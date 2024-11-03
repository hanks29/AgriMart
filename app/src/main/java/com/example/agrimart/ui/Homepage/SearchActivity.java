package com.example.agrimart.ui.Homepage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.adapter.CategoryAdapter;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.ActivitySearchBinding;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.SearchViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchViewModel searchViewModel;
    ActivitySearchBinding binding;
    List<Category> categories=new ArrayList<>();
    CategoryAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel= new ViewModelProvider(this).get(SearchViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setSearch(searchViewModel);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        }


        categoryAdapter = new CategoryAdapter(categories);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 4));
        binding.rvCategories.setAdapter(categoryAdapter);
        getCategories();
        List<Product> products = new ArrayList<>();
//        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
//        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
//        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
//        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
//        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
//        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvProducts.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        ProductAdapter productAdapter = new ProductAdapter(products, product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            startActivity(intent);
        });
        binding.rvProducts.setAdapter(productAdapter);
        searchViewModel.setProducts(products);

    }

    private void getCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Category> categories = new ArrayList<>();
        db.collection("categories")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categories.addAll(task.getResult().toObjects(Category.class));
                this.categories.addAll(categories);
                categoryAdapter.notifyDataSetChanged();

            }
        });

    }
}