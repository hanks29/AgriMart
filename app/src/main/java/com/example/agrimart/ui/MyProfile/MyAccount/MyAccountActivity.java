package com.example.agrimart.ui.MyProfile.MyAccount;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.example.agrimart.ui.Account.SignInActivity;
import com.example.agrimart.ui.MainActivity;
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
import java.util.Calendar;

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
    LinearLayout myUserSex,myUserDateBirth,myUserName;
    String[] genderOptions = {"Nam", "Nữ", "Khác"};
    private static final int EDIT_USER_REQUEST_CODE = 1;

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
        myUserSex = findViewById(R.id.my_user_sex);
        myUserDateBirth = findViewById(R.id.my_user_date_birth);
        myUserName = findViewById(R.id.my_user_name);
    }

    void addEvent() {
        btn_back.setOnClickListener(v -> onBackPressed());
        btn_account_img.setOnClickListener(v -> openImageChooser()); // cập nhật ảnh user
        myUserSex.setOnClickListener(v -> openGenderOptions()); // cập nhật giới tính
        myUserDateBirth.setOnClickListener(v -> openDatePicker());
        myUserName.setOnClickListener(v -> openEditUser());
    }

    private void openEditUser()
    {
        Intent intent = new Intent(MyAccountActivity.this, EditUserActivity.class);
        startActivityForResult(intent, EDIT_USER_REQUEST_CODE);
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Cập nhật TextView với ngày đã chọn
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            user_date_birth_text.setText(selectedDate);
        }, year, month, day);

        // Tạo AlertDialog để bọc DatePickerDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(datePickerDialog.getDatePicker());
        builder.setPositiveButton("Hoàn thành", (dialog, which) -> {
            datePickerDialog.getDatePicker().clearFocus(); // Đảm bảo lấy ngày chính xác
            datePickerDialog.onClick(dialog, DatePickerDialog.BUTTON_POSITIVE);

            String selectedDate = user_date_birth_text.getText().toString();
            saveBirthDateToFirestore(selectedDate);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        // Hiển thị AlertDialog
        builder.show();
    }

    private void saveBirthDateToFirestore(String date) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("birthDate", date)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(MyAccountActivity.this, "Đã cập nhật ngày sinh", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MyAccountActivity.this, "Lỗi khi cập nhật ngày sinh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openGenderOptions() {
        // Tạo một TextView cho tiêu đề và tùy chỉnh nó
        TextView title = new TextView(this);
        title.setText("Giới tính");
        title.setPadding(0, 30, 0, 30); // Khoảng cách trên và dưới
        title.setGravity(Gravity.CENTER); // Căn giữa
        title.setTextSize(20); // Kích thước chữ
        title.setTypeface(null, Typeface.BOLD); // Đặt kiểu chữ đậm nếu cần

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(title); // Đặt tiêu đề tùy chỉnh

        // Thiết lập danh sách tùy chọn
        builder.setItems(genderOptions, (dialog, which) -> {
            user_sex_text.setText(genderOptions[which]);
            saveGenderToFirestore(genderOptions[which]);
        });

        builder.show();
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void saveGenderToFirestore(String gender) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("sex", gender)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(MyAccountActivity.this, "Đã cập nhật giới tính", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MyAccountActivity.this, "Lỗi khi cập nhật giới tính: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra nếu kết quả trả về từ EditUserActivity
        if (requestCode == EDIT_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            loadUserInfo(); // Gọi phương thức để tải lại thông tin người dùng
        }

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
