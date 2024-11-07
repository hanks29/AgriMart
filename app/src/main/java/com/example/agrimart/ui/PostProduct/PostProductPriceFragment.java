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
import com.example.agrimart.data.model.AddressRequestProduct;
import com.example.agrimart.data.model.PostProduct;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.databinding.FragmentPostProductPriceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostProductPriceFragment extends Fragment {

    private TextView textViewDate;
    private ProductRequest product=new ProductRequest();
    private List<Uri> imageUris;

    private String categoryName;
    private int count = 0;
    public PostProductPriceFragment() {
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
            Log.d("ImageUris", "onCreate: "+product.getName());
            categoryName=getArguments().getString("category");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_product_price, container, false);
        binding = FragmentPostProductPriceBinding.bind(view);
        AppCompatButton btnPostProduct = view.findViewById(R.id.btnPostPro);






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
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child("products")
                .child(String.valueOf(System.currentTimeMillis()));

        imageRef.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getImageDownloadUrl(imageRef);
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getImageDownloadUrl(StorageReference imageRef) {
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            count++;
            product.getImageUrls().add(uri.toString());

            if (count == imageUris.size()) {
                saveProductToFirestore();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Lỗi khi lấy URL ảnh", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveProductToFirestore() {
        product.setPrice(Double.parseDouble(binding.edtPrice.getText().toString()));
        product.setQuantity(Integer.parseInt(binding.edtQuantity.getText().toString()));
        product.setStatus("pending");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        product.setCreatedAt(currentDate);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            product.setStoreId(user.getUid());
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newProductRef = db.collection("products").document();
        String productId = newProductRef.getId();
        product.setProductId(productId);

        getUserStoreAddressAndSave(db, newProductRef);
    }

    private void getUserStoreAddressAndSave(FirebaseFirestore db, DocumentReference newProductRef) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            db.collection("users")
                    .whereEqualTo("email", currentUser.getEmail())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            Map<String, Object> storeAddress = (Map<String, Object>) document.get("store_address");

                            if (storeAddress != null) {
                                String city = (String) storeAddress.get("city");
                                String district = (String) storeAddress.get("district");
                                String ward = (String) storeAddress.get("ward");
                                String street = (String) storeAddress.get("street");

                                product.setAddress(new AddressRequestProduct(city, district, ward, street));
                                saveProduct(newProductRef);
                            }
                        } else {
                            Log.d("Firestore", "Không tìm thấy tài liệu người dùng.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Lỗi khi lấy địa chỉ cửa hàng", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveProduct(DocumentReference newProductRef) {
        newProductRef.set(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Tạo sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    // Intent để mở ProductPreviewActivity nếu cần
                    Intent intent = new Intent(requireContext(), ProductPreviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("product", product);
                    intent.putExtras(bundle);
                    intent.putExtra("category", categoryName);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Tạo sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                });
    }
}
