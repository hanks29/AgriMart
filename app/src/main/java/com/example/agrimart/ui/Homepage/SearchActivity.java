package com.example.agrimart.ui.Homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.data.adapter.CategoryAdapter;
import com.example.agrimart.data.adapter.ProductAdapter;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.ActivitySearchBinding;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchViewModel searchViewModel;
    ActivitySearchBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel= new ViewModelProvider(this).get(SearchViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setSearch(searchViewModel);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(R.drawable.vegetable, "Rau củ quả"));
        categories.add(new Category(R.drawable.fruit, "Trái cây"));
        categories.add(new Category(R.drawable.frash_fruits, "Ngũ cốc và hạt"));
        categories.add(new Category(R.drawable.frash_fruits, "Gia vị"));
        categories.add(new Category(R.drawable.frash_fruits, "Mật ong"));
        categories.add(new Category(R.drawable.frash_fruits, "Trà"));
        categories.add(new Category(R.drawable.frash_fruits, "Cây cảnh"));
        categories.add(new Category(R.drawable.frash_fruits, "Khác"));

        CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 4));
        binding.rvCategories.setAdapter(categoryAdapter);

        List<Product> products = new ArrayList<>();
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000"));

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
}