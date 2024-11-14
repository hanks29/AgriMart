package com.example.agrimart.ui.MyProfile.MyAccount;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.example.agrimart.viewmodel.MyAccountViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
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
    //ImageButton btn_back;
    ImageView user_image;
    FrameLayout btn_account_img;
    private static final int PICK_IMAGE_REQUEST = 1;
    LinearLayout myUserSex, myUserDateBirth, myUserName, changePassword, myPhoneNumber;
    String[] genderOptions = {"Nam", "Nữ", "Khác"};
    private static final int EDIT_USER_REQUEST_CODE = 1;
    private static final int EDIT_NUMBER_PHONE_REQUEST_CODE = 10;
    private MyAccountViewModel viewModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_account);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();



        addLoadData();

        addEvent();
    }

    void addControl() {
        user_name_text = findViewById(R.id.user_name_text);
        my_phone_number_text = findViewById(R.id.my_phone_number_text);
        email_text = findViewById(R.id.email_text);
        user_date_birth_text = findViewById(R.id.user_date_birth_text);
        user_sex_text = findViewById(R.id.user_sex_text);
        //btn_back = findViewById(R.id.btn_back);
        toolbar = findViewById(R.id.header_toolbar);
        user_image = findViewById(R.id.user_image);
        btn_account_img = findViewById(R.id.fl_my_account_img);
        myUserSex = findViewById(R.id.my_user_sex);
        myUserDateBirth = findViewById(R.id.my_user_date_birth);
        myUserName = findViewById(R.id.my_user_name);
        changePassword = findViewById(R.id.change_password);
        myPhoneNumber = findViewById(R.id.my_phone_number);
        viewModel = new ViewModelProvider(this).get(MyAccountViewModel.class);
    }

    void addLoadData()
    {
        // lấy thông tin user
        viewModel.getUser().observe(this, user -> {

            if (user != null) {
                user_name_text.setText(user.getFullName());
                my_phone_number_text.setText(user.getPhoneNumber());
                email_text.setText(user.getEmail());
                user_sex_text.setText(user.getSex());
                user_date_birth_text.setText(user.getBirthDate());
                viewModel.loadImage(MyAccountActivity.this, user_image);

            }
        });

        // kiểm tra đăng nhập
        viewModel.getIsChangePassword().observe(this, isChangePassword -> {
            if(isChangePassword)
            {
                changePassword.setVisibility(View.GONE);
            }
        });
    }

    void addEvent() {
        //btn_back.setOnClickListener(v -> back());
        toolbar.setNavigationOnClickListener(v -> back());
        btn_account_img.setOnClickListener(v -> openImageChooser()); // cập nhật ảnh user
        myUserSex.setOnClickListener(v -> openGenderOptions()); // cập nhật giới tính
        myUserDateBirth.setOnClickListener(v -> openDatePicker());
        myUserName.setOnClickListener(v -> openEditUser());
        changePassword.setOnClickListener(v -> openVerifyAccount());
        myPhoneNumber.setOnClickListener(v -> openEditNumberPhone());
    }

    void openEditNumberPhone()
    {
        Intent intent = new Intent(MyAccountActivity.this, EditNumberPhoneActivity.class);
        startActivityForResult(intent, EDIT_NUMBER_PHONE_REQUEST_CODE);
    }

    void back()
    {
        setResult(RESULT_OK); // Đặt kết quả trả về
        finish();
    }


    private void openVerifyAccount()
    {
        Intent intent = new Intent(MyAccountActivity.this, VerifyWithPasswordActivity.class);
        startActivity(intent);
    }

    private void openEditUser()
    {
        Intent intent = new Intent(MyAccountActivity.this, EditUserNameActivity.class);
        startActivityForResult(intent, EDIT_USER_REQUEST_CODE);
    }

    private void openDatePicker() {
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate layout tùy chỉnh
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calendar, null);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        // Đặt ngày cho DatePicker
        final Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

        builder.setView(dialogView)
                .setPositiveButton("Hoàn thành", (dialog, which) -> {
                    // Lấy ngày đã chọn
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth() + 1; // Month is 0-based
                    int year = datePicker.getYear();
                    String selectedDate = day + "/" + month + "/" + year;

                    // Cập nhật TextView với ngày đã chọn
                    user_date_birth_text.setText(selectedDate);
                    viewModel.saveBirthDateToFirestore(selectedDate);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        // Giới hạn ngày phải bé hơn hoặc bằng ngày hiện hành
        datePicker.setMaxDate(calendar.getTimeInMillis());

        // Hiển thị AlertDialog
        builder.show();
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

        // Kiểm tra nếu genderOptions không null
        if (genderOptions != null) {
            // Thiết lập danh sách tùy chọn
            builder.setItems(genderOptions, (dialog, which) -> {
                user_sex_text.setText(genderOptions[which]);
                // Gọi phương thức trong ViewModel để lưu giới tính
                viewModel.saveGenderToFirestore(genderOptions[which]);
            });
        } else {
            Toast.makeText(this, "Không có tùy chọn giới tính", Toast.LENGTH_SHORT).show();
        }

        // Hiển thị dialog
        builder.show();
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

        if (requestCode == EDIT_NUMBER_PHONE_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.loadUserInfo(); // Gọi phương thức để tải lại thông tin người dùng
        }
        // Kiểm tra nếu kết quả trả về từ EditUserActivity
        if (requestCode == EDIT_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            viewModel.loadUserInfo(); // Gọi phương thức để tải lại thông tin người dùng
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
                viewModel.uploadImageToFirebase(MyAccountActivity.this, croppedImageUri, new MyAccountViewModel.ImageUploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        // Gọi loadUserInfo và loadImage sau khi ảnh đã được tải lên thành công
                        viewModel.loadUserInfo();
                        viewModel.loadImage(MyAccountActivity.this, user_image);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(MyAccountActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (resultCodeCrop == UCrop.RESULT_ERROR) {
                Toast.makeText(this, "Error cropping image", Toast.LENGTH_SHORT).show();
            }
        }

    }








}
