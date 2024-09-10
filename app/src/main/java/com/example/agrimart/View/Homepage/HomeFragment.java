package com.example.agrimart.View.Homepage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.Controller.CategoryAdapter;
import com.example.agrimart.Controller.ProductAdapter;
import com.example.agrimart.Model.Category;
import com.example.agrimart.Model.Product;
import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.databinding.FragmentHomeBinding;

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
        binding= FragmentHomeBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        List<Category> categories=new ArrayList<>();
        categories.add(new Category(R.drawable.fruit,"Trái cây"));
        categories.add(new Category(R.drawable.rau_cu,"Rau củ quả"));
        CategoryAdapter categoryAdapter=new CategoryAdapter(categories);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.rvCategories.setAdapter(categoryAdapter);

        List<Product> products=new ArrayList<>();
        products.add(new Product(R.drawable.ot,"Sầu riêng lan tỏa","45.000 đ/kg","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.ot,"Sầu riêng lan tỏa","45.000 đ/kg","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.ot,"Sầu riêng lan tỏa","45.000 đ/kg","200kg","20 người đang hỏi"));
        products.add(new Product(R.drawable.ot,"Sầu riêng lan tỏa","45.000 đ/kg","200kg","20 người đang hỏi"));
        ProductAdapter productAdapter=new ProductAdapter(products);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(),2));
        int spacingInPixels=getResources().getDimensionPixelSize(R.dimen.spacing);
        binding.rvProducts.addItemDecoration(new SpacesItemDecoration(16));
        binding.rvProducts.setAdapter(productAdapter);
        return view;
    }
}