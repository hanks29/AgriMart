package com.example.agrimart.ui.PostProduct;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.agrimart.R;
import com.example.agrimart.adapter.PostProductsAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.ProductResponse;
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
 * Use the {@link YourListProductCancelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourListProductCancelFragment extends Fragment {

    private PostProductsAdapter postProductsAdapter;
    private List<ProductResponse> productResponseList;

    private boolean isSelecting = false;

    FragmentYourProductListingsBinding binding;
    public YourListProductCancelFragment() {
        // Required empty public constructor
    }

    public static YourListProductCancelFragment newInstance() {
        return new YourListProductCancelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentYourProductListingsBinding.inflate(inflater, container, false);

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

        return binding.getRoot();
    }
    private void loadData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid=user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products")
                    .whereEqualTo("storeId",uid)
                    .whereEqualTo("status","reject")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("khanhne", "onComplete: "+task.getResult().size());
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