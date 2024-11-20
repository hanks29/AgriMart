package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderStoreAdapter extends RecyclerView.Adapter<OrderStoreAdapter.OrderStoreViewHolder> {
    private final List<Order> orderStoreList = new ArrayList<>();

    // Constructor
    public OrderStoreAdapter(List<Order> orderStoreList) {
        this.orderStoreList.addAll(orderStoreList);
    }

    // Update orders when data changes
    public void updateOrders(List<Order> newOrders) {
        orderStoreList.clear();
        orderStoreList.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_store, parent, false);
        return new OrderStoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderStoreViewHolder holder, int position) {
        // Get the current OrderStore
        Order orderStore = orderStoreList.get(position);

        List<Product> products = orderStore.getProducts();
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products);

        holder.recyclerViewItemOrder.setAdapter(productOrderAdapter);
        holder.recyclerViewItemOrder.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        // Set text data
        holder.tvStoreName.setText(orderStore.getStoreName());

        String translatedStatus;
        switch (orderStore.getStatus()) {
            case "pending":
                translatedStatus = "Chờ xác nhận";
                break;
            case "approved":
                translatedStatus = "Chờ giao hàng";
                break;
            case "delivering":
                translatedStatus = "Đang giao";
                break;
            case "delivered":
                translatedStatus = "Đã giao";
                break;
            case "cancel":
                translatedStatus = "Đã hủy";
                break;
            default:
                translatedStatus = "Không xác định";
                break;
        }
        holder.tvStatus.setText(translatedStatus);

        holder.tvTotalPrice.setText("Tổng số tiền: " + orderStore.getTotalPrice() + " VND");


        // Load product/store image (you can modify this depending on your image URL or resource)

    }

    @Override
    public int getItemCount() {
        return orderStoreList.size();
    }

    // ViewHolder class
    static class OrderStoreViewHolder extends RecyclerView.ViewHolder {

        TextView tvStoreName, tvStatus, tvTotalPrice;
        ImageView imageView;
        RecyclerView recyclerViewItemOrder;

        public OrderStoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvStatus = itemView.findViewById(R.id.status);
            tvTotalPrice = itemView.findViewById(R.id.total_price);
            imageView = itemView.findViewById(R.id.imageView9);
            recyclerViewItemOrder= itemView.findViewById(R.id.recyclerViewItemOrder);
        }
    }
}
