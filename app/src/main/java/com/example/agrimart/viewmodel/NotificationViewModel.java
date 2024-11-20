package com.example.agrimart.viewmodel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
    private final List<Notification> notificationList = new ArrayList<>();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public LiveData<List<Notification>> getNotificationsLiveData(String userId) {

        firestore.collection("notifications")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("NotificationViewModel", "Lỗi khi lấy dữ liệu: ", error);
                        return;
                    }

                    if (value != null) {
                        notificationList.clear();
                        for (DocumentChange docChange : value.getDocumentChanges()) {
                            if (docChange.getType() == DocumentChange.Type.ADDED) {
                                Notification notification = docChange.getDocument().toObject(Notification.class);
                                notificationList.add(notification);
                            }
                        }
                        notificationsLiveData.setValue(notificationList);
                    }
                });

        return notificationsLiveData;
    }
}
