package com.example.agrimart.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.agrimart.R;
import com.example.agrimart.adapter.OrderStoreAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.ui.Account.SignInActivity;
import com.example.agrimart.ui.Cart.CartFragment;
import com.example.agrimart.ui.Explore.ExploreFragment;
import com.example.agrimart.ui.Homepage.HomeFragment;
import com.example.agrimart.ui.MyProfile.MyProfileFragment;
import com.example.agrimart.ui.Notification.NotificationFragment;
import com.example.agrimart.ui.Payment.VnpayRefund;
import com.example.agrimart.viewmodel.NotificationViewModel;
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private NotificationViewModel notificationViewModel;
    private String userId;
    private static final String CHANNEL_ID = "default_channel";

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
                userId = currentUser.getUid();
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

        int selectedItemId = getIntent().getIntExtra("selected_item_id", R.id.home);
        if (selectedItemId != R.id.home) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        Order order = (Order) getIntent().getSerializableExtra("order");
        if (order != null) {
            refundOrder(order);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserInfo(mAuth.getCurrentUser());
        notificationViewModel = new NotificationViewModel(getApplication());
        notificationViewModel.createNotificationsForUser();
        notificationViewModel.createNotificationsForSeller();
        createNotificationChannel(this);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "This is the default notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
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
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            selectedFragment.setArguments(bundle);
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
            Log.e("FirebaseError", "Error getting user role", e);
        });
    }

    private void getUserInfo(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userId = documentSnapshot.getString("userId");
                Log.d("MainActivity", "User ID: " + userId);
            }
        }).addOnFailureListener(e -> {
            Log.e("FirebaseError", "Error getting user info", e);
        });
    }

    private void saveUserToSharedPreferences(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_role", role);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationPermission", "Notification permission granted");
            } else {
                Log.d("NotificationPermission", "Notification permission denied");
            }
        }
    }

    private void refundOrder(Order order) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(order.getOrderId()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isReturn = documentSnapshot.getBoolean("return");
                if (isReturn != null && isReturn) {
                    String vnpTxnRef = documentSnapshot.getString("vnpTxnRef");
                    order.setVnpTxnRef(vnpTxnRef);

                    new Thread(() -> {
                        try {
                            String vnp_TxnRef = order.getVnpTxnRef();
                            String transactionId = order.getTransactionId();
                            int totalPrice = order.getTotalPrice();
                            String formattedTransactionDate = OrderStoreAdapter.formatTimestampToVnpayDate(order.getTransactionDateMillis());

                            // Gửi yêu cầu hoàn tiền
                            String response = VnpayRefund.createRefundRequest(
                                    vnp_TxnRef,          // Mã giao dịch của merchant (txnRef)
                                    transactionId,       // Mã giao dịch từ VNPAY
                                    totalPrice,          // Số tiền hoàn
                                    formattedTransactionDate, // Ngày giao dịch gốc
                                    "Hoàn tiền cho đơn hàng " + order.getOrderId(), // Lý do hoàn tiền
                                    "admin"              // Người thực hiện
                            );

                            // Nếu hoàn tiền thành công
                            if (response.contains("\"vnp_ResponseCode\":\"00\"")) { // ResponseCode là 00 (Hoàn tiền thành công)
                                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(MainActivity.this, "Hoàn tiền thành công", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                // Nếu hoàn tiền không thành công
                                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(MainActivity.this, "Không thể hoàn tiền: " + response, Toast.LENGTH_SHORT).show();
                                });
                                Log.println(Log.ERROR, "Vnpayreturn", response);
                            }
                        } catch (Exception e) {
                            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(MainActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Đơn hàng không có yêu cầu trả hàng", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Lỗi khi lấy thông tin đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}