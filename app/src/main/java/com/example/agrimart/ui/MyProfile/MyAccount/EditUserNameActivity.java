package com.example.agrimart.ui.MyProfile.MyAccount;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserNameActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    ImageButton btnBack;
    ImageView userErase;
    TextView userText, txt_luu;
    private String originalUserName;
 // Định nghĩa hằng số


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user_name);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
        btnBack = findViewById(R.id.btn_back);
        userErase = findViewById(R.id.my_user_erase);
        userText = findViewById(R.id.user_text);
        txt_luu = findViewById(R.id.txt_luu);
    }

    void addEvent() {
        btnBack.setOnClickListener(v -> onBackPressed());
        userErase.setOnClickListener(v -> clearUserName());
        txt_luu.setOnClickListener(v -> saveUserNameToFirestore());

        // Thêm TextWatcher để theo dõi sự thay đổi trong userText
        userText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
                            originalUserName = document.getString("fullName");
                            // Cập nhật TextViews
                            userText.setText(originalUserName);
                        }
                    }
                    // Kiểm tra trạng thái của nút lưu
                    updateSaveButtonState();
                });
    }

    private void clearUserName() {
        userText.setText("");
        userErase.setEnabled(true);
    }

    private void saveUserNameToFirestore() {
        String name = userText.getText().toString().trim(); // Lấy tên người dùng và loại bỏ khoảng trắng ở đầu và cuối
        if (name.isEmpty()) {
            // Hiển thị thông báo nếu tên người dùng không có ký tự nào
            Toast.makeText(this, "Tên người dùng không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.length() > 100) {
            // Hiển thị thông báo nếu tên người dùng vượt quá 100 ký tự
            Toast.makeText(this, "Tên người dùng không được vượt quá 100 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu tên người dùng hợp lệ, lưu vào Firestore
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("fullName", name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Tên người dùng đã được lưu!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Đặt kết quả trả về
                        finish();
                    } else {
                        Toast.makeText(this, "Có lỗi xảy ra khi lưu tên người dùng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateSaveButtonState() {
        String currentUserName = userText.getText().toString().trim();
        boolean isEnabled = !currentUserName.equals(originalUserName) && !currentUserName.isEmpty();

        // Kiểm tra xem tên người dùng hiện tại có giống với tên gốc không
        txt_luu.setEnabled(isEnabled);

        if (isEnabled) {
            txt_luu.setTextColor(getResources().getColor(R.color.green)); // Màu khi kích hoạt
        } else {
            txt_luu.setTextColor(getResources().getColor(R.color.gray)); // Màu khi vô hiệu hóa
        }
    }
}
