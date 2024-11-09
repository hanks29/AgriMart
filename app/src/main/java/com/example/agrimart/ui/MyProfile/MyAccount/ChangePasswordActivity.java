package com.example.agrimart.ui.MyProfile.MyAccount;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.utils.Valid;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private AppCompatButton btn_changePass;
    private TextInputEditText edtPassword, edtConfirmPassword;
    private FirebaseAuth auth;
    private TextView tvPasswordError, tvConfirmPasswordError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        addEvent();
    }

    private void addControl() {
        btn_back = findViewById(R.id.btn_back);
        btn_changePass = findViewById(R.id.btn_changePass);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvConfirmPasswordError = findViewById(R.id.tvConfirmPasswordError);
    }

    private void addEvent() {
        btn_back.setOnClickListener(v -> onBackPressed());

        btn_changePass.setOnClickListener(v -> {
            String newPassword = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
//            if (!Valid.validatePass(newPassword,tvPasswordError)) {
//                tvPasswordError.setVisibility(View.VISIBLE);
//                Toast.makeText(this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
//                return;
//            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
                tvConfirmPasswordError.setText("Mật khẩu không khớp");
                tvConfirmPasswordError.setVisibility(View.VISIBLE);
                return;
            }

            changePassword(newPassword);
        });
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                            // Đóng Activity sau khi đổi mật khẩu thành công
                            setResult(RESULT_OK); // Đặt kết quả trả về
                            finish();
                        } else {
                            Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }
}
