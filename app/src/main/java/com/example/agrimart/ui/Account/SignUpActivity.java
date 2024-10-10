package com.example.agrimart.ui.Account;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.viewmodel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {
    private EditText edtPhoneNumber;
    private TextView tvPhoneNumberError;
    private Button btnSignUp;
    private ImageButton btnBack;
    private TextView tvHaveAccount;
    private ProgressBar progressBar;
    private static final int SMS_PERMISSION_CODE = 100;

    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        initViewModel();
    }

    private void initViews() {
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        tvPhoneNumberError = findViewById(R.id.tvPhoneNumberError);
        btnSignUp = findViewById(R.id.btn_signin);
        btnBack = findViewById(R.id.btn_back);
        tvHaveAccount = findViewById(R.id.haveAccount);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // Quan sát kết quả gửi OTP
        viewModel.getOtpSentSuccess().observe(this, success -> {
            if (success) {
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                Intent intent = new Intent(SignUpActivity.this, EnterOTPActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                //intent.putExtra("otp", viewModel.getOtpValue());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Lỗi khi gửi OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
