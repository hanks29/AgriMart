package com.example.agrimart.ui.PostProduct;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.agrimart.R;
import com.example.agrimart.data.model.PostProduct;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.databinding.FragmentPostProductPriceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostProductPriceFragment extends Fragment {

    private TextView textViewDate;
    private ProductRequest product=new ProductRequest();
    private List<Uri> imageUris;

    private int count = 0;
    public PostProductPriceFragment() {
        // Required empty public constructor
    }
    private FragmentPostProductPriceBinding binding;

    public static PostProductPriceFragment newInstance(String param1, String param2) {
        PostProductPriceFragment fragment = new PostProductPriceFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product= (ProductRequest) getArguments().getSerializable("postProduct");
            product.setImageUrls(new ArrayList<>());
            imageUris=getArguments().getParcelableArrayList("imageUris");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_product_price, container, false);
        binding = FragmentPostProductPriceBinding.bind(view);
        AppCompatButton btnPostProduct = view.findViewById(R.id.btnPostPro);
        // Set the date 5 days from now to TextView





        binding.btnPostPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();
            }
        });
        return binding.getRoot();
    }




    private void uploadImages() {

        for (Uri imageUri : imageUris) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("products").child(String.valueOf(System.currentTimeMillis()));
            imageRef.putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.isComplete()) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            count++;
                                            product.getImageUrls().add(uri.toString());
                                            if(count==imageUris.size()){
                                                product.setPrice(Double.parseDouble(binding.edtPrice.getText().toString()));
                                                product.setQuantity(Integer.parseInt(binding.edtQuantity.getText().toString()));
                                                product.setHeight(Double.parseDouble(binding.edtHeight.getText().toString()));
                                                product.setWidth(Double.parseDouble(binding.edtWidth.getText().toString()));
                                                product.setLength(Double.parseDouble(binding.edtLength.getText().toString()));
                                                product.setWeight(Double.parseDouble(binding.edtWeight.getText().toString()));
                                                product.setStatus("approved");
                                                FirebaseFirestore db=FirebaseFirestore.getInstance();


                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                if(user!=null){
                                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    product.setStoreId(uid);
                                                }
                                                db.collection("products").add(product)
                                                        .addOnSuccessListener(documentReference -> {
                                                            Toast.makeText(requireContext(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(requireContext(), YourProductListingsActivity.class);
                                                            startActivity(intent);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(requireContext(), "Đăng bài thất bại", Toast.LENGTH_SHORT).show();
                                                        });
                                            }



                                        }
                                    });

                                }
                            }
                        }
                    });

        }

    }
}
