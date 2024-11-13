package com.example.agrimart.ui.PostProduct;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.agrimart.R;
import com.example.agrimart.adapter.PostProductsAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.ProductResponse;
import com.example.agrimart.databinding.FragmentPendingListBinding;
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
 * Use the {@link PendingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private PostProductsAdapter postProductsAdapter;
    private List<ProductResponse> productResponseList;

    private boolean isSelecting = false;
    private FragmentPendingListBinding binding;

    public PendingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingListFragment newInstance(String param1, String param2) {
        PendingListFragment fragment = new PendingListFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPendingListBinding.inflate(inflater, container, false);
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

        binding.editPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postProductsAdapter.setSelecting(!isSelecting);

                if(!isSelecting) {
                    binding.editPro.setText("Hủy");
                    binding.editDelete.setVisibility(View.VISIBLE);
                }
                else {
                    binding.editPro.setText("Chỉnh sửa");
                    binding.editDelete.setVisibility(View.GONE);
                }
                isSelecting=!isSelecting;
                postProductsAdapter.selectedProducts.clear();
            }
        });

        binding.editDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(ProductResponse product:postProductsAdapter.selectedProducts){

                    productResponseList.remove(product);
                    deleteProducts(product.getProductId());
                }
                postProductsAdapter.selectedProducts.clear();
                postProductsAdapter.setSelecting(false);
                postProductsAdapter.notifyDataSetChanged();

            }
        });
    }

    private void loadData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid=user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products")
                    .whereEqualTo("storeId",uid)
                    .whereEqualTo("status","pending")
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

        productRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                });
    }
}