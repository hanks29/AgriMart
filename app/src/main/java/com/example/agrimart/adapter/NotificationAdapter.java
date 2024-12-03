package com.example.agrimart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Notification;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.OrderInformationActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(notification.getTimestamp()));
        holder.time.setText(formattedDate);

        Glide.with(holder.itemView.getContext())
                .load(notification.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.logo_icon)
                .into(holder.notificationImage);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, OrderInformationActivity.class);

            String message = notification.getMessage();
            String status = "";
            if (message.contains("chờ người bán xác nhận")) {
                status = "pending";
            } else if (message.contains("đang được đóng gói")) {
                status = "approved";
            } else if (message.contains("trong quá trình vận chuyển")) {
                status = "delivering";
            } else if (message.contains("đã được giao thành công")) {
                status = "delivered";
            } else if (message.contains("đã bị huỷ")) {
                status = "canceled";
            } else if (message.contains("trả hàng")) {
                status = "return";
            }

            intent.putExtra("status", status);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;
        ImageView notificationImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            time = itemView.findViewById(R.id.notification_time);
            notificationImage = itemView.findViewById(R.id.notification_image);
        }
    }
}