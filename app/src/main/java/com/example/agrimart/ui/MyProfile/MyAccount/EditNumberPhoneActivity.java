package com.example.agrimart.ui.MyProfile.MyAccount;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNumberPhoneActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    AppCompatButton btnUpdate;
    EditText numberPhone;
    ImageButton btn_back;
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_number_phone);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        addEvent();
    }

    void addControl()
    {
        btnUpdate = findViewById(R.id.btnUpdate);
        numberPhone = findViewById(R.id.user_phone);
        btn_back = findViewById(R.id.btn_back);
        errorMessage = findViewById(R.id.errorMessage);

        btnUpdate.setEnabled(false);

        numberPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần thực hiện gì trước khi văn bản thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra điều kiện để kích hoạt hoặc vô hiệu hóa nút
                validatePhoneNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần thực hiện gì sau khi văn bản đã thay đổi
            }
        });
    }

    void addEvent()
    {
        btn_back.setOnClickListener(v -> onBackPressed());
        btnUpdate.setOnClickListener(v -> saveNumberPhoneToFirestore());
    }

    private void saveNumberPhoneToFirestore() {
        String phone = numberPhone.getText().toString().trim(); // Lấy số điện thoại và loại bỏ khoảng trắng ở đầu và cuối

        // Nếu số điện thoại hợp lệ, lưu vào Firestore
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("phoneNumber", phone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Số điện thoại đã được lưu!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Đặt kết quả trả về
                        finish();
                    } else {
                        Toast.makeText(this, "Có lỗi xảy ra khi lưu số điện thoại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validatePhoneNumber(String phone) {
        // Kiểm tra các điều kiện
        if (phone.length() == 10) {
            if (phone.startsWith("0")) {
                btnUpdate.setEnabled(true); // Kích hoạt nút nếu số điện thoại hợp lệ
                btnUpdate.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                errorMessage.setVisibility(View.GONE);
            } else {
                btnUpdate.setEnabled(false); // Vô hiệu hóa nút
                btnUpdate.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                errorMessage.setVisibility(View.VISIBLE);
            }
        } else {
            btnUpdate.setEnabled(false); // Vô hiệu hóa nút nếu không đủ 10 số
            btnUpdate.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        }
    }

}