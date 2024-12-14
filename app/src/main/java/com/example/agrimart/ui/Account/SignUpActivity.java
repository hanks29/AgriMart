package com.example.agrimart.ui.Account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
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
    private EditText edtEmail, edtPassword, edtName, edtConfirmPassword;
    private TextView tvEmailError, tvPasswordError, tvNameError, tvConfirmPasswordError;
    private Button btnSignUp;
    private ImageButton btnBack;
    private TextView tvHaveAccount;
    private ProgressBar progressBar;

    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        addControls();
        addEvents();
    }

    private void addControls() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtName = findViewById(R.id.edtUsername);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvNameError = findViewById(R.id.tvNameError);
        tvConfirmPasswordError = findViewById(R.id.tvConfirmPasswordError);
        btnSignUp = findViewById(R.id.btn_signin);
        btnBack = findViewById(R.id.btn_back);
        tvHaveAccount = findViewById(R.id.haveAccount);
        progressBar = findViewById(R.id.progressBar);
    }

    private void addEvents() {
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

        btnSignUp.setOnClickListener(v -> {
            if (validateInputs()) {
                progressBar.setVisibility(View.VISIBLE);
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String name = edtName.getText().toString().trim();

                viewModel.signUpWithEmail(email, password, name);
                Intent intent = new Intent(SignUpActivity.this, VerifyActivity.class);
                intent.putExtra("fullname", name);
                startActivity(intent);
                finish();
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

    private boolean validateInputs() {
        boolean isValid = true;

        tvEmailError.setVisibility(View.GONE);
        tvPasswordError.setVisibility(View.GONE);
        tvNameError.setVisibility(View.GONE);
        tvConfirmPasswordError.setVisibility(View.GONE);

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String name = edtName.getText().toString().trim();

        if (email.isEmpty()) {
            tvEmailError.setText("Email không được để trống");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmailError.setText("Email không hợp lệ");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (password.isEmpty()) {
            tvPasswordError.setText("Mật khẩu không được để trống");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*\\d.*")) {
            tvPasswordError.setText("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa và số");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tvConfirmPasswordError.setText("Không được để trống");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            tvConfirmPasswordError.setText("Mật khẩu xác nhận không khớp");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (name.isEmpty()) {
            tvNameError.setText("Tên không được để trống");
            tvNameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (name.length() < 3 || name.matches(".*\\d.*")) {
            tvNameError.setText("Tên phải có ít nhất 3 ký tự và không được chứa số");
            tvNameError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

}
