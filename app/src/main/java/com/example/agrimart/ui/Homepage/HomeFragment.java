package com.example.agrimart.ui.Homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.data.adapter.CategoryAdapter;
import com.example.agrimart.data.adapter.ProductAdapter;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.FragmentHomeBinding;
import com.example.agrimart.ui.Notification.NotificationActivity;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ImageButton btnSearch, btnNotification;
    TextView search;

    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);
        int numberOfColumns = 4;
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

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
        rvCategories.setAdapter(categoryAdapter);

        List<Product> products = new ArrayList<>();
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000", "200kg", "20 người đang hỏi"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000", "200kg", "20 người đang hỏi"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000", "200kg", "20 người đang hỏi"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000", "200kg", "20 người đang hỏi"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000", "200kg", "20 người đang hỏi"));
        products.add(new Product(R.drawable.banana, "Organic Bananas", "45.000", "200kg", "20 người đang hỏi"));

        ProductAdapter productAdapter = new ProductAdapter(products, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            startActivity(intent);
        });

        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProducts.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        binding.rvProducts.setAdapter(productAdapter);

        addControls(view);
        addEvents();

        return view;
    }

    private void addEvents() {
        btnSearch.setOnClickListener(View -> {
            Intent intent=new Intent(getActivity(), SearchActivity.class);
            getActivity().startActivity(intent);
        });

        btnNotification.setOnClickListener(View -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });

        search.setOnClickListener(View -> {
            Intent intent=new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });
    }

    private void addControls(View view) {
        btnSearch = view.findViewById(R.id.search_icon);
        btnNotification = view.findViewById(R.id.notification_icon);
        search = view.findViewById(R.id.search_input);
    }

    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}