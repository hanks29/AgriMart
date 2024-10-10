package com.example.agrimart.ui.Account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agrimart.R;
import com.example.agrimart.ui.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

public class EnterOTPActivity extends AppCompatActivity {
    private TextInputEditText edtOtp;
    private Button btnConfirm;
    private TextView tvResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otpactivity);

        edtOtp = findViewById(R.id.edtOtp);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvResend = findViewById(R.id.tvResend);
        TextView tvDescription = findViewById(R.id.tv_description);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String otp = getIntent().getStringExtra("otp");

    }
}