package com.example.agrimart.ui.Account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.agrimart.R;

public class SignUpActivity extends AppCompatActivity {
    ImageButton btnBack;
    TextView tvHaveAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addControls();
        addEvents();
    }

    public void addControls() {
        btnBack = findViewById(R.id.btn_back);
        tvHaveAccount = findViewById(R.id.haveAccount);
    }

    public void addEvents() {
        btnBack.setOnClickListener(v -> {
            SignUpActivity.this.finish();
        });

        tvHaveAccount.setOnClickListener(v -> {
            SignUpActivity.this.startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });
    }
}