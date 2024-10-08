package com.example.agrimart.ui.Account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.viewmodel.SignUpViewModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private SignUpViewModel viewModel;
    private EditText edtPhoneNumber, edtPassword, edtConfirmPassword;
    private TextView tvPhoneNumberError, tvPasswordError, tvConfirmPasswordError;
    private Button btnSignUp;
    private ImageButton btnBack;
    private TextView tvHaveAccount;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        tvPhoneNumberError = findViewById(R.id.tvPhoneNumberError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvConfirmPasswordError = findViewById(R.id.tvConfirmPasswordError);
        btnSignUp = findViewById(R.id.btn_signin);
        btnBack = findViewById(R.id.btn_back);
        tvHaveAccount = findViewById(R.id.haveAccount);

        btnSignUp.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (validatePhoneNumber(phoneNumber) & validatePassword(password) & validateConfirmPassword(password, confirmPassword)) {
                sendOtp(phoneNumber, password);
            }
        });

        btnBack.setOnClickListener(v -> finish());

        tvHaveAccount.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(SignUpActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                Intent intent = new Intent(SignUpActivity.this, PhoneNumberAuthActivity.class);
                intent.putExtra("phoneNumber", edtPhoneNumber.getText().toString().trim());
                intent.putExtra("password", edtPassword.getText().toString().trim());
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
            }
        };
    }

    private void sendOtp(String phoneNumber, String password) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 10) {
            tvPhoneNumberError.setText("Số điện thoại phải có 10 số");
            tvPhoneNumberError.setVisibility(View.VISIBLE);
            return false;
        }
        tvPhoneNumberError.setVisibility(View.GONE);
        return true;
    }

    private boolean validatePassword(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");
        if (!passwordPattern.matcher(password).matches()) {
            tvPasswordError.setText("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa và số");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        }
        tvPasswordError.setVisibility(View.GONE);
        return true;
    }

    private boolean validateConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            tvConfirmPasswordError.setText("Mật khẩu xác nhận không khớp");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            return false;
        }
        tvConfirmPasswordError.setVisibility(View.GONE);
        return true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}