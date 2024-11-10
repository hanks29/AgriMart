package com.example.agrimart.ui.Homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.adapter.CategoryAdapter;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.ui.SearchPage.SearchFragment;
import com.example.agrimart.viewmodel.HomeFragmentViewModel;
import com.example.agrimart.viewmodel.SearchViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel searchViewModel;
    private ProductAdapter productAdapter;
    private List<Category> categories = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private List<Product> products = new ArrayList<>();
    private RecyclerView rvCategories;
    private androidx.appcompat.widget.SearchView searchInput;
    private LinearLayout ln1;
    private FrameLayout container;
    private ImageButton searchBy;
    private boolean isSearchByProduct = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageButton btnBack = findViewById(R.id.btnBack);
        rvCategories = findViewById(R.id.rvCategories);
        searchInput = findViewById(R.id.search_input);
        ln1 = findViewById(R.id.ln1);
        container = findViewById(R.id.container);
        searchBy = findViewById(R.id.searchBy);

        searchBy.setOnClickListener(v -> showSearchByDialog());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        }

        categoryAdapter = new CategoryAdapter(categories, category -> {
            searchViewModel.searchProductsByCategory(category.getId());
            ln1.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new SearchFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            hideKeyboard();
            searchInput.clearFocus();
        });

        rvCategories.setLayoutManager(new GridLayoutManager(this, 4));
        rvCategories.setAdapter(categoryAdapter);
        getCategories();


        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        searchInput.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                searchInput.clearFocus();
                if (query.isEmpty()) {
                    ln1.setVisibility(View.VISIBLE);
                    container.setVisibility(View.GONE);
                } else {
                    if (isSearchByProduct) {
                        searchViewModel.searchProductsByName(query);
                    } else {
                        searchViewModel.searchProductsByStoreName(query);
                    }
                    ln1.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, new SearchFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchInput.setOnSearchClickListener(v -> {
            getSupportFragmentManager().popBackStack();
            ln1.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        });

        searchInput.setOnCloseListener(() -> {
            ln1.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
            return false;
        });

        searchInput.setOnClickListener(v -> {
            getSupportFragmentManager().popBackStack();
            ln1.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        });

        searchInput.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getSupportFragmentManager().popBackStack();
                ln1.setVisibility(View.VISIBLE);
                container.setVisibility(View.GONE);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .orderBy("id")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categories.addAll(task.getResult().toObjects(Category.class));
                        categoryAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showSearchByDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tìm kiếm theo");

        String[] options = {"Sản phẩm", "Cửa hàng"};

        int checkedItem = isSearchByProduct ? 0 : 1;

        builder.setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
            isSearchByProduct = (which == 0);
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (isSearchByProduct) {
                searchInput.setQueryHint(getString(R.string.tim_kiem));
                searchViewModel.searchProductsByName(searchInput.getQuery().toString());
            } else {
                searchInput.setQueryHint("Tìm kiếm cửa hàng");
                searchViewModel.searchProductsByStoreName(searchInput.getQuery().toString());
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}