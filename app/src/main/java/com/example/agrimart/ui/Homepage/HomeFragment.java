package com.example.agrimart.ui.Homepage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.adapter.CategoryAdapter;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.FragmentHomeBinding;
import com.example.agrimart.ui.Explore.ExploreFragment;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.HomeFragmentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    ImageButton btnSearch;
    TextView search;
    HomeFragmentViewModel viewModel;
    FragmentHomeBinding binding;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        binding.setViewmodel(viewModel);
        View view = binding.getRoot();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        viewModel.getData();
        viewModel.getProducts();

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                CategoryAdapter categoryAdapter = new CategoryAdapter(categories, category -> {
                    // Khi chọn một category, chuyển đến ExploreFragment và chọn mục category
                    ExploreFragment newFragment = new ExploreFragment(category.getId());

                    // Thay đổi mục đã chọn trong BottomNavigationView
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
                    if (bottomNavigationView != null) {
                        bottomNavigationView.setSelectedItemId(R.id.category); // Chọn mục category
                    }

                    loadFragment(newFragment);
                });

                binding.rvCategories.setAdapter(categoryAdapter);
                binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
                Log.d(TAG, "Categories loaded: " + categories.size());
            }
        });


        viewModel.products.observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                ProductAdapter productAdapter = new ProductAdapter(products, product -> {
                    try {
                        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                        intent.putExtra("product", product);
                        intent.putExtra("storeId", product.getStoreId());
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                });
                binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
                binding.rvProducts.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                binding.rvProducts.setAdapter(productAdapter);
            }
        });

        addControls(view);
        addEvents();

        return view;
    }

    private void addEvents() {
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            getActivity().startActivity(intent);
        });

        search.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });
    }

    private void addControls(View view) {
        btnSearch = view.findViewById(R.id.search_icon);
        search = view.findViewById(R.id.search_input);
    }

    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}