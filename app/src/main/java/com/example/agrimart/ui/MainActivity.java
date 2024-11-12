package com.example.agrimart.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.agrimart.R;
import com.example.agrimart.ui.Account.SignInActivity;
import com.example.agrimart.ui.Cart.CartFragment;
import com.example.agrimart.ui.Explore.ExploreFragment;
import com.example.agrimart.ui.Homepage.HomeFragment;
import com.example.agrimart.ui.MyProfile.MyProfileFragment;
import com.example.agrimart.ui.Notification.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                navigateToHome();
                getUserRole(currentUser);
            } else {
                clearLoginState();
                navigateToLogin();
            }
        } else {
            navigateToLogin();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserInfo(mAuth.getCurrentUser());
    }

    private void navigateToHome() {
        loadFragment(new HomeFragment());
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearLoginState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.home) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.category) {
            selectedFragment = new ExploreFragment();
        } else if (item.getItemId() == R.id.notification) {
            selectedFragment = new NotificationFragment();
        } else if (item.getItemId() == R.id.profile) {
            selectedFragment = new MyProfileFragment();
        } else if (item.getItemId() == R.id.cart) {
            selectedFragment = new CartFragment();
        } else {
            return false;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        return true;
    }

    private void getUserRole(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if (role != null) {
                    saveUserToSharedPreferences(role);
                }
            }
        }).addOnFailureListener(e -> {
        });
    }

    private void getUserInfo(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if (role != null) {
                    saveUserToSharedPreferences(role);
                }
            }
        }).addOnFailureListener(e -> {
        });
    }

    private void saveUserToSharedPreferences(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_role", role);
        editor.apply();
    }
}