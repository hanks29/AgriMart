package com.example.agrimart.ui.MyProfile.MyStore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.databinding.ActivityRegisterSellerBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterSellerActivity extends AppCompatActivity {
    private ActivityRegisterSellerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding= ActivityRegisterSellerBinding.inflate(LayoutInflater.from(this));
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnRegister.setOnClickListener(v -> {
            updateUserInformation();
        });
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia=registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), uri->{
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        binding.floatingActionButton.setOnClickListener(v -> {
            ImagePicker.with(RegisterSellerActivity.this)
                    .galleryOnly()
                    .start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode==RESULT_OK && data!=null){
            binding.imgAvt.setImageURI(data.getData());
        }else if (resultCode == RESULT_CANCELED) {
            Log.d("ImagePicker", "Người dùng hủy chọn ảnh");
        }else {
            Log.w("ImagePicker", "Truong hop khac");
        }
    }

    private void updateUserInformation() {
        boolean isSuccessful=false;
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        Map<String,Object> storeAddress=new HashMap<>();
        storeAddress.put("city",binding.edtCity.getText().toString());
        storeAddress.put("district",binding.edtDistrict.getText().toString());
        storeAddress.put("ward",binding.edtWard.getText().toString());
        storeAddress.put("street",binding.edtStreet.getText().toString());

        Map<String,Object> updates=new HashMap<>();
        updates.put("store_phone_number",binding.edtPhoneNumber.getText().toString());
        updates.put("store_address",storeAddress);
        updates.put("store_name",binding.edtNameStore.getText().toString());
        updates.put("role","seller");

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("role", "seller");

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
               db.collection("users").document(user.getUid())
                       .update(updates)
                       .addOnSuccessListener(aVoid -> {
                           Log.d("Register", "DocumentSnapshot successfully updated!");
                           Toast.makeText(this, "Đăng kí người bán thành công", Toast.LENGTH_SHORT).show();
                           Intent intent=new Intent(RegisterSellerActivity.this, MyStoreActivity.class);
                           startActivity(intent);
                       })
                       .addOnFailureListener(e -> {
                           Log.w("Register", "Error updating document", e);
                       });
        }



    }
}