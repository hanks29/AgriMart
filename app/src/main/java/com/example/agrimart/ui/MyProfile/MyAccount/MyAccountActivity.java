package com.example.agrimart.ui.MyProfile.MyAccount;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MyAccountActivity extends AppCompatActivity {

    TextView user_name_text, my_phone_number_text, email_text, user_sex_text, user_date_birth_text;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ImageButton btn_back;
    ImageView user_image;
    FrameLayout btn_account_img;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_account);

        // Khởi tạo Firebase Auth, Firestore và Storage
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("userImages"); // Thay đổi đường dẫn lưu ảnh

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        loadUserInfo();
        addEvent();
    }

    void addControl() {
        user_name_text = findViewById(R.id.user_name_text);
        my_phone_number_text = findViewById(R.id.my_phone_number_text);
        email_text = findViewById(R.id.email_text);
        user_date_birth_text = findViewById(R.id.user_date_birth_text);
        user_sex_text = findViewById(R.id.user_sex_text);
        btn_back = findViewById(R.id.btn_back);
        user_image = findViewById(R.id.user_image);
        btn_account_img = findViewById(R.id.fl_my_account_img);
    }

    void addEvent(){
        btn_back.setOnClickListener(v-> onBackPressed());
        btn_account_img.setOnClickListener(v -> openImageChooser());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Crop ảnh trước khi tải lên
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
            UCrop.of(imageUri, destinationUri)
                    .withAspectRatio(1, 1) // Tỉ lệ khung hình 1:1
                    .withMaxResultSize(512, 512) // Kích thước tối đa
                    .start(this);
        }

        if (requestCode == UCrop.REQUEST_CROP) {
            final int resultCodeCrop = resultCode;
            final Intent resultData = data;
            if (resultCodeCrop == RESULT_OK) {
                Uri croppedImageUri = UCrop.getOutput(resultData);
                // Tải ảnh đã crop lên Firebase
                uploadImageToFirebase(croppedImageUri);
            } else if (resultCodeCrop == UCrop.RESULT_ERROR) {
                Toast.makeText(this, "Error cropping image", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void uploadImageToFirebase(Uri imageUri) {
        String userId = auth.getCurrentUser().getUid();
        StorageReference fileReference = FirebaseStorage.getInstance().getReference("users/" + userId + "/profile.jpg");

        try {
            // Giảm kích thước và chất lượng ảnh
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // 50 là mức nén chất lượng
            byte[] data = baos.toByteArray();

            // Tải lên ảnh nén
            UploadTask uploadTask = fileReference.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                saveImageUrlToFirestore(uri.toString());
                Glide.with(this).load(uri).apply(RequestOptions.circleCropTransform()).into(user_image);
            })).addOnFailureListener(e -> {
                Toast.makeText(MyAccountActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error getting image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void saveImageUrlToFirestore(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("userImage", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Đã cập nhật thành công
                    Toast.makeText(MyAccountActivity.this, "Đã cập nhật hình ảnh thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lưu URL
                    Toast.makeText(MyAccountActivity.this, "Error updating image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void loadUserInfo() {
        String userId = auth.getCurrentUser().getUid(); // Lấy UID của người dùng hiện tại
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Lấy dữ liệu từ document
                            String name = document.getString("fullName");
                            String phone = document.getString("phone");
                            String email = document.getString("email");
                            String sex = document.getString("sex");
                            String birthDate = document.getString("birthDate");
                            String urlImage = document.getString("userImage");

                            // Cập nhật TextViews
                            user_name_text.setText(name);
                            my_phone_number_text.setText(phone);
                            email_text.setText(email);
                            user_sex_text.setText(sex);
                            user_date_birth_text.setText(birthDate);

                            if (urlImage != null && !urlImage.isEmpty()) {
                                Glide.with(this)
                                        .load(urlImage)
                                        .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh khi tải lên
                                        .placeholder(R.drawable.account) // ảnh mặc định nếu URL rỗng
                                        .into(user_image);
                            }
                        } else {
                            // Xử lý khi không tìm thấy tài liệu
                            user_name_text.setText("User not found");
                        }
                    } else {
                        // Xử lý lỗi khi lấy dữ liệu
                        user_name_text.setText("Error getting user info");
                    }
                });
    }
}
