package com.example.agrimart.ui.MyProfile.MyAccount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyWithPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText edtPassword;
    private AppCompatButton btn_dongY;
    private ImageButton btn_back;
    private static final int CHANGE_PASS_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_with_password);

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
        edtPassword = findViewById(R.id.edtPassword);
        btn_dongY = findViewById(R.id.btn_dongY);
        btn_back = findViewById(R.id.btn_back);
    }


    private void addEvent() {
        btn_back.setOnClickListener(v -> onBackPressed());

        btn_dongY.setOnClickListener(v -> {
            String password = edtPassword.getText().toString().trim();
            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }
            verifyPassword(password);
        });
    }


    private void verifyPassword(String password) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Tạo xác thực với mật khẩu
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

            // Thực hiện xác thực
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Xác thực thành công", Toast.LENGTH_SHORT).show();
                    // Thực hiện hành động tiếp theo khi xác thực thành công
                    openChangePassword();
                } else {
                    Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openChangePassword()
    {
        Intent intent = new Intent(VerifyWithPasswordActivity.this, ChangePasswordActivity.class);
        startActivityForResult(intent, CHANGE_PASS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_PASS_REQUEST_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }
}