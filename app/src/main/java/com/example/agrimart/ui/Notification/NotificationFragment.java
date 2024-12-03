package com.example.agrimart.ui.Notification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private ImageView ivEmpty;

    public NotificationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        } else {
            Log.e(TAG, "Không tìm thấy userId");
        }

        recyclerView = view.findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ivEmpty = view.findViewById(R.id.ivEmpty);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.getNotifications(userId);
        notificationViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                ivEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                ivEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new NotificationAdapter(notifications);
                recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }
}