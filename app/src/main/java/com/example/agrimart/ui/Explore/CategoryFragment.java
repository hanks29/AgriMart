package com.example.agrimart.ui.Explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agrimart.R;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Product;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private String categoryName;

    public CategoryFragment(String categoryName) {
        this.categoryName = categoryName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<Product> products = new ArrayList<>();

//        if (categoryName.equals("Rau củ quả")) {
//            products.add(new Product(R.drawable.vegetable, "Cà chua", "20.000đ"));
//            products.add(new Product(R.drawable.vegetable, "Cà rốt", "15.000đ"));
//            products.add(new Product(R.drawable.vegetable, "Bí đỏ", "10.000đ"));
//        }
//        else if (categoryName.equals("Trái cây")) {
//            products.add(new Product(R.drawable.apple, "Dâu tây", "30.000đ"));
//            products.add(new Product(R.drawable.apple, "Chuối", "10.000đ"));
//            products.add(new Product(R.drawable.apple, "Cam", "20.000đ"));
//        }

        ProductAdapter productAdapter = new ProductAdapter(products, product -> {
        });
        recyclerView.setAdapter(productAdapter);

        return view;
    }
}