package com.example.agrimart.ui.Notification;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.agrimart.R;
import com.example.agrimart.adapter.NotificationAdapter;
import com.example.agrimart.data.model.Notification;
import com.example.agrimart.viewmodel.NotificationViewModel;

import java.util.List;

public class NotificationFragment extends Fragment {
    private static final String ARG_USER_ID = "userId";
    private static final String TAG = "NotificationFragment";
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private NotificationViewModel notificationViewModel;
    private String userId;

    public NotificationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            Log.d(TAG, "User ID from arguments: " + userId);
        } else {
            Log.e(TAG, "Failed to get User ID from arguments");
        }

        recyclerView = view.findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.getNotifications(userId);
        notificationViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), notifications -> {
            Log.d(TAG, "Notifications received: " + notifications.size());
            adapter = new NotificationAdapter(notifications);
            recyclerView.setAdapter(adapter);
        });

        return view;
    }
}