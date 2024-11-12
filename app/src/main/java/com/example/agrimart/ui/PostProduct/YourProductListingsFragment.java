package com.example.agrimart.ui.PostProduct;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.Toast;

import com.example.agrimart.R;
import com.example.agrimart.adapter.PostProductsAdapter;
import com.example.agrimart.data.model.PostProduct;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.data.model.ProductResponse;
import com.example.agrimart.databinding.ActivityYourProductListingsBinding;
import com.example.agrimart.databinding.FragmentYourProductListingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

    private PostProductsAdapter postProductsAdapter;
    private List<ProductResponse> productResponseList;

    private FragmentYourProductListingsBinding binding;
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

    ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentYourProductListingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        productResponseList = new ArrayList<>();

        postProductsAdapter = new PostProductsAdapter(productResponseList,product -> {
            Intent intent = new Intent(getContext(), ProductPreviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", product);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(postProductsAdapter);
        loadData();



    }

    private void loadData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid=user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products")
                    .whereEqualTo("storeId",uid)
                    .whereEqualTo("status","available")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Product product = document.toObject(Product.class);
                                productResponseList.add(
                                        new ProductResponse(
                                                product.getName(),
                                                product.getPrice(),
                                                product.getCategory(),
                                                product.getDescription(),
                                                product.getQuantity(),
                                                product.getImages().get(0),
                                                product.getProduct_id()

                                        )
                                );

                            }
                            postProductsAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }
    private void deleteProducts(String productId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(productId);

        productRef.update("status", "delete")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                });
    }


}
