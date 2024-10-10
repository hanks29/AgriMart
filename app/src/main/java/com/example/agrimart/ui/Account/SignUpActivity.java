package com.example.agrimart.ui.Account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.utils.Valid;
import com.example.agrimart.viewmodel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword, edtName;
    private TextView tvEmailError, tvPasswordError, tvNameError;
    private Button btnSignUp;
    private ImageButton btnBack;
    private TextView tvHaveAccount;
    private ProgressBar progressBar;

    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        initViewModel();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtUsername);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvNameError = findViewById(R.id.tvNameError);
        btnSignUp = findViewById(R.id.btn_signin);
        btnBack = findViewById(R.id.btn_back);
        tvHaveAccount = findViewById(R.id.haveAccount);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        viewModel.getSignUpSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đăng ký thành công. Vui lòng kiểm tra email để xác nhận tài khoản.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, VerifyActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi đăng ký", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getEmailVerificationSent().observe(this, sent -> {
            if (sent) {
                Toast.makeText(this, "Mail xác nhận đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi gửi mail", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            if (Valid.validateInputs(email, password, name, tvEmailError, tvPasswordError, tvNameError)) {
                viewModel.signUpWithEmail(email, password);
            } else {
                Toast.makeText(this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        tvHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
