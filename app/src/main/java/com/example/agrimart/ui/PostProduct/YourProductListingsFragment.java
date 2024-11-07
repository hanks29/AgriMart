package com.example.agrimart.ui.PostProduct;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.R;
import com.example.agrimart.adapter.PostProductsAdapter;
import com.example.agrimart.data.model.PostProduct;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.data.model.ProductResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private List<ProductResponse> productResponseList;

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

        productResponseList = new ArrayList<>();

        postProductsAdapter = new PostProductsAdapter(productResponseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postProductsAdapter);
        loadData();
    }

    private void loadData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid=user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products")
                    .whereEqualTo("storeId",uid)
//                    .whereEqualTo("status","pending")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                ProductRequest product = document.toObject(ProductRequest.class);
                                String name=document.getData().get("name")!=null?document.getData().get("name").toString():"";
                                double price=0;
                                if(document.getData().get("price")!=null){
                                    price=Double.parseDouble(document.getData().get("price").toString());
                                }
                                List<String> images= (List<String>) document.getData().get("images");
                                String imageUrl=(images!=null && !images.isEmpty())?images.get(0):"https://firebasestorage.googleapis.com/v0/b/agri-mart-2342e.appspot.com/o/notfound.jpg?alt=media&token=40e61714-5a10-4352-918c-7e5e2643a1fe";

                                Log.d("khanekhan", document.getId() + " => " + document.getData());
                                productResponseList.add(
                                        new ProductResponse(
                                                name,
                                                price,
                                                imageUrl
                                        )
                                );
                            }
                            postProductsAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }
}
