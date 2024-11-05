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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.example.agrimart.viewmodel.VerifyWithPasswordViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class VerifyWithPasswordActivity extends AppCompatActivity {

    private VerifyWithPasswordViewModel viewModel;
    private TextInputEditText edtPassword;
    private AppCompatButton btn_dongY;
    private ImageButton btn_back;
    private static final int CHANGE_PASS_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_with_password);

        viewModel = new ViewModelProvider(this).get(VerifyWithPasswordViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        addEvent();
        observeViewModel();
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
            viewModel.verifyPassword(password, this);
        });
    }

    private void observeViewModel() {
        viewModel.isAuthenticationSuccessful().observe(this, isSuccess -> {
            if (isSuccess) {
                openChangePassword();
            }
        });
    }

    private void openChangePassword() {
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
