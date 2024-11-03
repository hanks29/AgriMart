package com.example.agrimart.ui.MyProfile.MyStore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.databinding.ActivityRegisterSellerBinding;
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
import java.util.Map;



public class RegisterSellerActivity extends AppCompatActivity {
    private ActivityRegisterSellerBinding binding;
    private Uri imageUri;
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
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Glide.with(this)
                                .load(uri).
                                into(binding.imgAvt);
                        imageUri=uri;
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
        editor.putString("user_role", "seller");
        editor.apply();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
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
                                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                                        if(user!=null) {
                                            db.collection("users").document(user.getUid())
                                                    .update(updates)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("Register", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(RegisterSellerActivity.this, "Đăng kí người bán thành công", Toast.LENGTH_SHORT).show();
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
                        }
                    }
                });





    }
}