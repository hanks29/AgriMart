package com.example.agrimart.ui.Homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.agrimart.R;
import com.example.agrimart.SpaceItemHorizontalDecoration;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.data.adapter.CategoryAdapter;
import com.example.agrimart.data.adapter.ProductAdapter;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.FragmentHomeBinding;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
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
        int spacingInPixels=getResources().getDimensionPixelSize(R.dimen.spacing);
        binding= FragmentHomeBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        List<Category> categories=new ArrayList<>();
        categories.add(new Category(R.drawable.vegetable,"Rau củ quả"));
        categories.add(new Category(R.drawable.fruit,"Trái cây"));
        CategoryAdapter categoryAdapter=new CategoryAdapter(categories);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.rvCategories.addItemDecoration(new SpaceItemHorizontalDecoration(spacingInPixels));
        binding.rvCategories.setAdapter(categoryAdapter);

        List<Product> products=new ArrayList<>();
        products.add(new Product(R.drawable.banana,"Organic Bananas","45.000","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.banana,"Organic Bananas","45.000","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.banana,"Organic Bananas","45.000","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.banana,"Organic Bananas","45.000","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.banana,"Organic Bananas","45.000","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.banana,"Organic Bananas","45.000","200kg","20 người đang hỏi"));

        ProductAdapter productAdapter=new ProductAdapter(products, product -> {
            Intent intent=new Intent(getContext(), ProductDetailActivity.class);
            startActivity(intent);
        });

        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(),2));

        binding.rvProducts.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        binding.rvProducts.setAdapter(productAdapter);
        return view;
    }

}