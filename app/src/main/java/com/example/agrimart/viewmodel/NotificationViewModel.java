package com.example.agrimart.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    private static final String TAG = "NotificationViewModel";
    private final MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
    }

    public void createNotificationsForUser() {
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
                            notificationForUser(dc, db);
                        }
                    }
                });
    }

    public void createNotificationsForSeller() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .whereEqualTo("storeId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("FirebaseError", "Error listening to orders", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            notificationForSeller(dc, db);
                        }
                    }
                });
    }

    private void notificationForUser(DocumentChange dc, FirebaseFirestore db) {
        String status = dc.getDocument().getString("status");
        List<String> notifiedStatuses = (List<String>) dc.getDocument().get("notifiedStatuses");
        String imageUrl = "android.resource://" + getApplication().getPackageName() + "/" + R.drawable.bell;
        long timestamp = System.currentTimeMillis();

        String title = "";
        String message = "";

        if (notifiedStatuses != null && notifiedStatuses.contains(status)) {
            return;
        }

        switch (status) {
            case "pending":
                title = "Đặt hàng thành công!";
                message = "Đơn hàng của bạn đang chờ người bán xác nhận.";
                break;
            case "approved":
                title = "Người bán đang chuẩn bị hàng";
                message = "Đơn hàng của bạn đang được đóng gói và sẽ sớm giao cho đơn vị vận chuyển.";
                break;
            case "delivering":
                title = "Đơn hàng đang trong quá trình vận chuyển";
                message = "Đơn hàng của bạn hiện đang trong quá trình vận chuyển.";
                break;
            case "delivered":
                title = "Đơn hàng đã được giao thành công";
                message = "Bạn có hài lòng với sản phẩm đã nhận?";
                break;
            case "canceled":
                title = "Đơn hàng đã bị huỷ";
                message = "Đơn hàng của bạn đã bị huỷ. Nếu bạn đã thanh toán bằng VNPAY số tiền đã thanh toán sẽ sớm được hoàn lại vào tài khoản của bạn.";
                break;
            case "return":
                title = "Yêu cầu trả hàng đã được chấp nhận";
                message = "Yêu cầu trả hàng của bạn đã được chấp nhận. Vui lòng chờ trong thời gian sớm nhất.";
                break;
        }

        if (!title.isEmpty() && !message.isEmpty()) {
            Notification notification = new Notification(title, message, timestamp, imageUrl);
            sendNotification(title, message);
            saveNotificationToFirestore(notification);

            db.collection("orders").document(dc.getDocument().getId())
                    .update("notifiedStatuses", FieldValue.arrayUnion(status))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Updated notifiedStatuses"))
                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error updating notifiedStatuses", e));
        }
    }

    private void notificationForSeller(DocumentChange dc, FirebaseFirestore db) {
        String status = dc.getDocument().getString("status");
        List<String> notifiedStatuses = (List<String>) dc.getDocument().get("notifiedStatuses");
        String imageUrl = "android.resource://" + getApplication().getPackageName() + "/" + R.drawable.bell;
        long timestamp = System.currentTimeMillis();

        String title = "";
        String message = "";

        if (notifiedStatuses != null && notifiedStatuses.contains(status)) {
            return;
        }

        switch (status) {
            case "pending":
                title = "Có đơn hàng mới";
                message = "Hãy xác nhận đơn hàng mới vừa được đặt";
                break;
            case "approved":
                title = "Chuẩn bị hàng";
                message = "Chuẩn bị đơn hàng để giao cho đơn vị vận chuyển";
                break;
            case "delivering":
                title = "Đơn bán đang trong quá trình vận chuyển";
                message = "Đơn bán hiện đang trong quá trình vận chuyển.";
                break;
            case "delivered":
                title = "Đơn bán đã được giao thành công";
                message = "Đơn bán đã được giao thành công cho khách hàng.";
                break;
            case "canceled":
                title = "Đơn hàng đã bị huỷ";
                message = "Đơn hàng đã bị huỷ.";
                break;
            case "return":
                title = "Trả hàng";
                message = "Khách hàng yêu cầu trả hàng.";
                break;
        }

        if (!title.isEmpty() && !message.isEmpty()) {
            Notification notification = new Notification(title, message, timestamp, imageUrl);
            sendNotification(title, message);
            saveNotificationToFirestore(notification);

            db.collection("orders").document(dc.getDocument().getId())
                    .update("notifiedStatuses", FieldValue.arrayUnion(status))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Updated notifiedStatuses"))
                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error updating notifiedStatuses", e));
        }
    }

    public void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplication(), "default_channel")
                .setSmallIcon(R.drawable.logo_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("NotificationViewModel", "Notification permission not granted");
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void saveNotificationToFirestore(Notification notification) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            notification.setUserId(currentUser.getUid());
            db.collection("notifications").add(notification)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Notification saved successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error saving notification", e);
                    });
        }
    }

    public void getNotifications(String userId) {
        Log.d(TAG, "Fetching notifications for user ID: " + userId);
        firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Notification notification = doc.toObject(Notification.class);
                            notifications.add(notification);
                        }
                        Log.d(TAG, "Fetched " + notifications.size() + " notifications");
                        notificationsLiveData.setValue(notifications);
                    } else {
                        Log.e(TAG, "Error fetching notifications", task.getException());
                    }
                });
    }

    public LiveData<List<Notification>> getNotificationsLiveData() {
        return notificationsLiveData;
    }
}