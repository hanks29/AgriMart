package com.example.agrimart.ui.PostProduct;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.R;
import com.example.agrimart.adapter.PostProductsAdapter;
import com.example.agrimart.data.model.PostProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YourProductListingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourProductListingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public YourProductListingsFragment() {
        // Required empty public constructor
    }

    public static YourProductListingsFragment newInstance(String param1, String param2) {
        YourProductListingsFragment fragment = new YourProductListingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private PostProductsAdapter postProductsAdapter;
    private List<PostProduct> postProductList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_product_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        postProductList = new ArrayList<>();

        // Populate postProductList with sample data or from a data source
        postProductList.add(new PostProduct("Chuối tiêu", "27.000đ/kg", R.drawable.banana));
        postProductList.add(new PostProduct("Táo đỏ", "35.000đ/kg", R.drawable.apple));
        postProductList.add(new PostProduct("Chuối tiêu", "27.000đ/kg", R.drawable.banana));
        postProductList.add(new PostProduct("Táo đỏ", "35.000đ/kg", R.drawable.apple));
        postProductList.add(new PostProduct("Chuối tiêu", "27.000đ/kg", R.drawable.banana));
        postProductList.add(new PostProduct("Táo đỏ", "35.000đ/kg", R.drawable.apple));
        postProductList.add(new PostProduct("Táo đỏ", "35.000đ/kg", R.drawable.apple));
        // Add more post products as needed

        postProductsAdapter = new PostProductsAdapter(postProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postProductsAdapter);
    }
}
