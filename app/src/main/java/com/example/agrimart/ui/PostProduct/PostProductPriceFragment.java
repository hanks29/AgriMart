package com.example.agrimart.ui.PostProduct;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.data.model.AddressRequestProduct;
import com.example.agrimart.data.model.PostProduct;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.databinding.FragmentPostProductPriceBinding;
import com.example.agrimart.ui.MyProfile.MyStore.EditProfileStoreActivity;
import com.example.agrimart.ui.MyProfile.MyStore.RegisterSellerActivity;
import com.example.agrimart.viewmodel.EditProfileStoreViewModel;
import com.example.agrimart.viewmodel.RegisterSellerViewModel;
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
import java.util.stream.Collectors;

public class PostProductPriceFragment extends Fragment {

    private TextView textViewDate;
    private ProductRequest product=new ProductRequest();
    private List<Uri> imageUris;

    private String categoryName;
    private int count = 0;
    public PostProductPriceFragment() {
    }
    private FragmentPostProductPriceBinding binding;

    private EditProfileStoreViewModel viewModel;

    private String selectedProvinceName="";
    private String provinceId;
    private String selectedDistrictName="";
    private String districtId;
    private String selectedWardName="";

    private String street="";

    private boolean isEdit=false;

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
        viewModel=new ViewModelProvider(this).get(EditProfileStoreViewModel.class);

        binding.btnPostPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.edtPrice.getText().toString().isEmpty() || binding.edtQuantity.getText().toString().isEmpty() || binding.edtUnit.getText().toString().isEmpty() || binding.edtStreet.getText().toString().isEmpty())
                {
                    Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadImages();
            }
        });
        viewModel.getProfileStore();
        disableSpinner();
        loadProvinces();
        viewModel.street.observe(requireActivity(), street -> {
            binding.edtStreet.setText(street);
        });
        binding.btnEdit.setOnClickListener(v -> {
            enableSpinner();
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

                            if (storeAddress != null && !isEdit) {
                                String city = (String) storeAddress.get("city");
                                String district = (String) storeAddress.get("district");
                                String ward = (String) storeAddress.get("ward");
                                String street = (String) storeAddress.get("street");

                                product.setAddress(new AddressRequestProduct(city, district, ward, street));
                                product.setUnit(binding.edtUnit.getText().toString());
                                saveProduct(newProductRef);
                            }
                            else{
                                street=binding.edtStreet.getText().toString();
                                product.setUnit(binding.edtUnit.getText().toString());
                                product.setAddress(new AddressRequestProduct(selectedProvinceName, selectedDistrictName, selectedWardName, street));
                                saveProduct(newProductRef);
                                product.setAddress(new AddressRequestProduct(selectedProvinceName, selectedDistrictName, selectedWardName, street));
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


    private void disableSpinner(){
        binding.spinnerCity.setEnabled(false);
        binding.spinnerDistrict.setEnabled(false);
        binding.spinnerWard.setEnabled(false);
    }

    private void enableSpinner(){
        binding.spinnerCity.setEnabled(true);
        binding.spinnerDistrict.setEnabled(true);
        binding.spinnerWard.setEnabled(true);
    }

    private void loadProvinces(){
        viewModel.getProvinces();
        viewModel.provinces.observe(requireActivity(), provinces -> {
            List<String> provinceNames = provinces.stream().map(province -> province.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerCity, provinceNames, position -> {
                provinceId=provinces.get(position).getIdProvince();
                selectedProvinceName = provinces.get(position).getName();
                loadDistrict(provinceId);
            });
            String selectedProvince = viewModel.province.getValue();
            if (selectedProvince != null) {
                binding.spinnerCity.setSelection(findProvincePosition(provinceNames, selectedProvince));

            }
        });


    }
    private void loadDistrict(String provinceId){
        viewModel.getDistricts(provinceId);
        viewModel.districts.observe(requireActivity(), districts -> {
            List<String> districtNames=districts.stream().map(district -> district.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerDistrict,districtNames,position -> {
                districtId=districts.get(position).getIdDistrict();
                selectedDistrictName=districts.get(position).getName();
                loadWard(districtId);
            });
            String selectedDistrict = viewModel.district.getValue();
            if (selectedDistrict != null) {
                binding.spinnerDistrict.setSelection(findDistrictPosition(districtNames, selectedDistrict));
            }
        });
    }

    private void loadWard(String districtId){
        viewModel.getWard(districtId);
        viewModel.wards.observe(requireActivity(), wards -> {
            List<String> wardNames=wards.stream().map(ward -> ward.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerWard,wardNames,position -> selectedWardName=wards.get(position).getName());
            String selectedWard = viewModel.ward.getValue();
            if (selectedWard != null) {
                binding.spinnerWard.setSelection(findWardPosition(wardNames, selectedWard));
            }
        });

    }

    private void setupSpinner(Spinner spinner, List<String> names, PostProductPriceFragment.OnItemSelectedListener2 onItemSelected) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected.onItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }
    private interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private int findProvincePosition(List<String> provinces, String provinceName) {
        for (int i = 0; i < provinces.size(); i++) {
            if (provinces.get(i).equals(provinceName)) {
                return i;
            }
        }
        return 0;
    }

    private int findDistrictPosition(List<String> districts, String districtName) {
        for (int i = 0; i < districts.size(); i++) {
            if (districts.get(i).equals(districtName)) {
                return i;
            }
        }
        return 0;
    }

    private int findWardPosition(List<String> wards, String wardName) {
        for (int i = 0; i < wards.size(); i++) {
            if (wards.get(i).equals(wardName)) {
                return i;
            }
        }
        return 0;
    }
    private interface OnItemSelectedListener2 {
        void onItemSelected(int position);
    }
}
