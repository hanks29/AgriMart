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
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.FragmentHomeBinding;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.HomeFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    ImageButton btnSearch;
    TextView search;
    HomeFragmentViewModel viewModel;
    FragmentHomeBinding binding;

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
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        viewModel=new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        binding.setViewmodel(viewModel);
        View view = binding.getRoot();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

//        if (getActivity() != null) {
//            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
//        }
        viewModel.getData();
        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
                binding.rvCategories.setAdapter(categoryAdapter);
                binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
                Log.d("duy1", "onCreateView: " + categories.size());
            }
        });


        List<Product> products = new ArrayList<>();
        products.add(new Product(R.drawable.banana, "Chuối nhà trồng", "45.000"));
        products.add(new Product(R.drawable.banana, "Chuối nhà trồng", "45.000"));
        products.add(new Product(R.drawable.banana, "Chuối nhà trồng", "45.000"));
        products.add(new Product(R.drawable.banana, "Chuối nhà trồng", "45.000"));
        products.add(new Product(R.drawable.banana, "Chuối nhà trồng", "45.000"));
        products.add(new Product(R.drawable.banana, "Chuối nhà trồng", "45.000"));

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

        search.setOnClickListener(View -> {
            Intent intent=new Intent(getContext(), SearchActivity.class);
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