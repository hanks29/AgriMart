package com.example.agrimart.ui.MyProfile.MyStore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.databinding.ActivityRegisterSellerBinding;
import com.example.agrimart.ui.MyProfile.MyAddress.GetAddressActivity;
import com.example.agrimart.viewmodel.RegisterSellerViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class RegisterSellerActivity extends AppCompatActivity {
    private ActivityRegisterSellerBinding binding;
    private RegisterSellerViewModel viewModel;
    private Uri imageUri;
    private String selectedProvinceName="";
    private String provinceId;
    private String selectedDistrictName="";
    private String districtId;
    private String selectedWardName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewModel= new ViewModelProvider(this).get(RegisterSellerViewModel.class);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_register_seller);
        binding.setViewModel(viewModel);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadProvince();
        binding.btnRegister.setOnClickListener(v -> {
            if(binding.edtStreet.getText().toString().isEmpty() &&
                    binding.edtNameStore.getText().toString().isEmpty()||
                    binding.edtPhoneNumber.getText().toString().isEmpty()||
                    Objects.equals(selectedDistrictName, "") ||
                    Objects.equals(selectedProvinceName, "")||
                    Objects.equals(selectedWardName, "") ||
                    Objects.isNull(imageUri)
            ) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }else{
                updateUserInformation();
            }
//            updateUserInformation();
        });
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Glide.with(this)
                                .load(uri).
                                into(binding.imgAvt);
                        imageUri=uri;
                        Log.d("PhotoPicker", "Selected URI: " + uri.toString());
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        binding.floatingActionButton.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void updateUserInformation() {
        boolean isSuccessful=false;
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        Map<String,Object> storeAddress=new HashMap<>();
//        storeAddress.put("city",selectedProvinceName);
//        storeAddress.put("district",selectedDistrictName);
//        storeAddress.put("ward",selectedWardName);
//        storeAddress.put("street",binding.edtStreet.getText().toString());

        Map<String,Object> updates=new HashMap<>();
        updates.put("store_phone_number",binding.edtPhoneNumber.getText().toString());
        updates.put("store_address",storeAddress);
        updates.put("store_name",binding.edtNameStore.getText().toString());
        updates.put("role","seller");

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_role", "seller");

        Map<String,Object> userAddress=new HashMap<>();
        userAddress.put("city",selectedProvinceName);
        userAddress.put("district",selectedDistrictName);
        userAddress.put("ward",selectedWardName);
        userAddress.put("street",binding.edtStreet.getText().toString());

        updates.put("store_address",userAddress);
        editor.apply();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(user.getUid()).child(String.valueOf(System.currentTimeMillis()));
        storageRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.isComplete()){
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        updates.put("store_avatar",uri.toString());
                                        if(user!=null) {
                                            db.collection("users").document(user.getUid())
                                                    .update(updates)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("Register", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(RegisterSellerActivity.this, "Tạo cửa hàng thành công", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(RegisterSellerActivity.this, MyStoreActivity.class);
                                                        startActivity(intent);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.w("Register", "Error updating document", e);
                                                    });
                                        }

                                    }
                                });
                            }
                        }else{
                            Log.d("Register", "Error uploading image: " + task.getException());
                        }
                    }
                });





    }

    private void loadProvince(){
        viewModel.getProvinces();
        viewModel.provinces.observe(this, provinces -> {
            List<String> provinceNames = provinces.stream().map(province -> province.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerCity, provinceNames, position -> {
                provinceId=provinces.get(position).getIdProvince();
                selectedProvinceName = provinces.get(position).getName();
                loadDistrict(provinceId);
            });
        });
    }

    private void loadDistrict(String provinceId){
        viewModel.getDistricts(provinceId);
        viewModel.districts.observe(this, districts -> {
            List<String> districtNames=districts.stream().map(district -> district.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerDistrict,districtNames,position -> {
                districtId=districts.get(position).getIdDistrict();
                selectedDistrictName=districts.get(position).getName();
                loadWard(districtId);
            });
        });
    }

    private void loadWard(String districtId){
        viewModel.getWard(districtId);
        viewModel.wards.observe(this, wards -> {
            List<String> wardNames=wards.stream().map(ward -> ward.getName()).collect(Collectors.toList());
            setupSpinner(binding.spinnerWard,wardNames,position -> selectedWardName=wards.get(position).getName());
        });
    }


    private void setupSpinner(Spinner spinner, List<String> names, RegisterSellerActivity.OnItemSelectedListener onItemSelected) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
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
}