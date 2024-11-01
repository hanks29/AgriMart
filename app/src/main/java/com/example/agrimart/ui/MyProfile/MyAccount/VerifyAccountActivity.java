package com.example.agrimart.ui.MyProfile.MyAccount;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyAccountActivity extends AppCompatActivity {

    ImageButton btn_back;
    LinearLayout id_VerifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        addEvent();
    }

    void addControl()
    {
        btn_back = findViewById(R.id.btn_back);
        id_VerifyPassword = findViewById(R.id.id_VerifyPassword);

    }

    void addEvent()
    {
        btn_back.setOnClickListener(v -> onBackPressed());
        id_VerifyPassword.setOnClickListener(v -> openVerifyWithPassword());
    }


    private void openVerifyWithPassword()
    {
        Intent intent = new Intent(VerifyAccountActivity.this, VerifyWithPasswordActivity.class);
        startActivity(intent);
    }


}