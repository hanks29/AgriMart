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
import android.util.Log;
import android.view.MenuItem;
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
import com.example.agrimart.data.model.Notification;
import com.example.agrimart.ui.Account.SignInActivity;
import com.example.agrimart.ui.Cart.CartFragment;
import com.example.agrimart.ui.Explore.ExploreFragment;
import com.example.agrimart.ui.Homepage.HomeFragment;
import com.example.agrimart.ui.MyProfile.MyProfileFragment;
import com.example.agrimart.ui.Notification.NotificationFragment;
import com.example.agrimart.viewmodel.NotificationViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    NotificationViewModel notificationViewModel;

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

        int selectedItemId = getIntent().getIntExtra("selected_item_id", R.id.home);
        if (selectedItemId != R.id.home) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        createNotificationChannel();
        createNotificationsForUser();
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
                String userId = documentSnapshot.getString("userId");
            }
        }).addOnFailureListener(e -> {
        });
    }

    private void saveUserToSharedPreferences(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_role", role);
        editor.apply();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "Channel for default notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    public void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel")
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationsForUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("orders")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("FirebaseError", "Error listening to orders", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                // Kiểm tra đơn hàng mới
                                String status = dc.getDocument().getString("status");
                                if ("pending".equals(status)) {
                                    String orderCode = dc.getDocument().getString("order_code");
                                    String imageUrl = dc.getDocument().getString("image_url");
                                    long timestamp = System.currentTimeMillis();

                                    String title = "Đặt hàng thành công!";
                                    String message = "Đơn hàng của bạn đang được xử lý và sẽ sớm chuyển đến đơn vị vận chuyển.";

                                    if (!title.isEmpty() && !message.isEmpty()) {
                                        Notification notification = new Notification(title, message, timestamp, imageUrl);
                                        sendNotification(title, message);
                                        saveNotificationToFirestore(notification);
                                    }
                                }
                            }

                            if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                String status = dc.getDocument().getString("status");
                                String orderCode = dc.getDocument().getString("order_code");
                                String imageUrl = dc.getDocument().getString("image_url");
                                long timestamp = System.currentTimeMillis();

                                String title = "";
                                String message = "";

                                switch (status) {
                                    case "pending":
                                        title = "Đặt hàng thành công!";
                                        message = "Đơn hàng của bạn đang được xử lý và sẽ sớm chuyển đến đơn vị vận chuyển.";
                                        break;
                                    case "approved":
                                        title = "Đơn hàng đang trên đường đến bạn!";
                                        message = "Đơn hàng của bạn đã được giao cho đơn vị vận chuyển và đang trên đường đến địa chỉ của bạn. Vui lòng theo dõi để biết thời gian giao hàng dự kiến.";
                                        break;
                                    case "delivering":
                                        title = "Đơn hàng đang trong quá trình vận chuyển";
                                        message = "Đơn hàng của bạn hiện đang trong quá trình vận chuyển. Bạn có thể theo dõi tình trạng giao hàng trong mục Theo dõi đơn hàng.";
                                        break;
                                    case "delivered":
                                        title = "Đơn hàng đã được giao thành công";
                                        message = "Đơn hàng của bạn đã được giao thành công. Bạn có hài lòng với sản phẩm đã nhận? Để lại đánh giá của bạn để giúp người dùng khác hiểu hơn về sản phẩm nhé.";
                                        break;
                                    case "cancelled":
                                        title = "Yêu cầu huỷ đơn hàng đã được chấp nhận";
                                        message = "Yêu cầu huỷ đơn hàng đã được chấp nhận. Số tiền đã thanh toán sẽ được hoàn lại vào tài khoản của bạn trong thời gian sớm nhất nếu bạn thanh toán bằng VNPAY.";
                                        break;
                                    case "delivery_failed":
                                        title = "Giao hàng thất bại";
                                        message = "Đơn hàng của bạn giao không thành công. Đơn vị vận chuyển sẽ thực hiện giao hàng lại trong thời gian sớm nhất.";
                                        break;
                                }

                                if (!title.isEmpty() && !message.isEmpty()) {
                                    Notification notification = new Notification(title, message, timestamp, imageUrl);
                                    sendNotification(title, message);  // Gửi thông báo
                                    saveNotificationToFirestore(notification);  // Lưu vào Firestore
                                }
                            }
                        }
                    }
                });
    }

    public void saveNotificationToFirestore(Notification notification) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            notification.setUserId(currentUser.getUid());
            db.collection("notifications").add(notification)
                    .addOnSuccessListener(documentReference -> {
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }
}