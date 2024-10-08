package com.example.agrimart.ui.Account;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agrimart.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberAuthActivity extends AppCompatActivity {

    private static final String TAG = "PhoneNumberAuthActivity";
    private FirebaseAuth mAuth;
    private EditText edtOtp;
    private Button btnVerifyOtp;
    private ImageButton btnBack;
    private TextView tvDescription;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_auth);

        mAuth = FirebaseAuth.getInstance();
        edtOtp = findViewById(R.id.edtOTP);
        btnVerifyOtp = findViewById(R.id.btn_confirm);
        btnBack = findViewById(R.id.btn_back);
        tvDescription = findViewById(R.id.tv_description);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String password = getIntent().getStringExtra("password");
        mVerificationId = getIntent().getStringExtra("verificationId");

        tvDescription.setText("Nhập mã vừa được gửi đến số điện thoại " + phoneNumber);

        btnVerifyOtp.setOnClickListener(v -> {
            String code = edtOtp.getText().toString().trim();
            verifyOtp(mVerificationId, code);
        });

        btnBack.setOnClickListener(v -> finish());

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneNumberAuthActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(PhoneNumberAuthActivity.this, "SMS quota exceeded", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    Toast.makeText(PhoneNumberAuthActivity.this, "reCAPTCHA verification failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void verifyOtp(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(PhoneNumberAuthActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(PhoneNumberAuthActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}