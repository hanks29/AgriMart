package com.example.agrimart.ui.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.ui.MainActivity;
import com.example.agrimart.viewmodel.SignInViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private SignInViewModel signInViewModel;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private GoogleSignInOptions gso;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    private TextView tvDonthaveAccount;
    private TextView forgotPass;
    private Button btnSignIn;
    private Button btnSignInGoogle;
    private TextView edtPhoneNumber, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                navigateToMain();
                return;
            }
        }

        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        signInViewModel.firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Đăng nhập bị từ chối", Toast.LENGTH_SHORT).show();
                }
            });

        addControls();
        addEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInViewModel.userLiveData.observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addControls() {
        tvDonthaveAccount = findViewById(R.id.dontHaveAccount);
        forgotPass = findViewById(R.id.forgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
    }

    public void addEvents() {
        tvDonthaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        forgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                signInViewModel.signInWithPhoneNumber(phoneNumber);
            } else {
                Toast.makeText(SignInActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignInGoogle.setOnClickListener(v -> {
            Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}