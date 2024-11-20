package com.example.agrimart.ui.Notification;

import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private static final String ARG_USER_ID = "user_id";
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private NotificationViewModel notificationViewModel;
    private String userId;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance(String userId) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }

        recyclerView = view.findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        notificationViewModel.getNotificationsLiveData(userId).observe(getViewLifecycleOwner(), notifications -> {
            notificationList.clear();
            notificationList.addAll(notifications);
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}