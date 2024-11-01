package com.example.agrimart.ui.Account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.ui.MainActivity;
import com.example.agrimart.viewmodel.SignUpViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyActivity extends AppCompatActivity {
    private SignUpViewModel viewModel;
    private FirebaseAuth mAuth;
    private Handler handler;
    private Runnable checkEmailVerifiedRunnable;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        handler = new Handler();

        Button btnResendEmail = findViewById(R.id.btnResendEmail);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        fullname = getIntent().getStringExtra("fullname");

        btnResendEmail.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                viewModel.sendEmailVerification(user);
                Toast.makeText(this, "Email xác nhận đã được gửi lại", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(VerifyActivity.this, SignInActivity.class));
            finish();
        });

        checkEmailVerification();
    }

    private void checkEmailVerification() {
        checkEmailVerifiedRunnable = new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(task -> {
                        if (user.isEmailVerified()) {
                            viewModel.saveUserToFirestore(user, fullname);
                            Toast.makeText(VerifyActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(VerifyActivity.this, MainActivity.class));
                            finish();
                        } else {
                            handler.postDelayed(checkEmailVerifiedRunnable, 3000);
                        }
                    });
                }
            }
        };
        handler.post(checkEmailVerifiedRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkEmailVerifiedRunnable);
    }
}