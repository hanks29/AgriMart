package com.example.agrimart.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.MyProfile.MyRating.ProductRatingActivity;
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrderStoreAdapter extends RecyclerView.Adapter<OrderStoreAdapter.OrderStoreViewHolder> {
    private final List<Order> orderStoreList = new ArrayList<>();
    private OrderStatusFragmentViewModel viewModel;

    String po;

    // Constructor
    public OrderStoreAdapter(List<Order> orderStoreList, OrderStatusFragmentViewModel viewModel) {
        this.orderStoreList.addAll(orderStoreList);
        this.viewModel = viewModel;
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
        po = String.valueOf(position);

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
                holder.btnBuy.setText("Hủy đơn hàng");
                break;
            case "approved":
                translatedStatus = "Chờ giao hàng";
                holder.btnBuy.setText("Đã nhận hàng");
                break;
            case "delivered":
                translatedStatus = "Hoàn thành";
                if (!orderStore.isCheckRating()) {
                    holder.btnBuy.setText("Đánh giá");
                    holder.btnDetail.setVisibility(View.GONE);
                }
                break;
            case "cancel":
                translatedStatus = "Đã hủy";
                break;
            default:
                translatedStatus = "Không xác định";
                break;
        }
        holder.tvStatus.setText(translatedStatus);

        // Thêm listener cho btnBuy
        holder.btnBuy.setOnClickListener(v -> cancelOrder(holder, orderStore));  // Truyền đúng item vào đây

        holder.tvTotalPrice.setText("Tổng số tiền: " + orderStore.getTotalPrice() + " VND");
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
        AppCompatButton btnBuy, btnDetail;

        public OrderStoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvStatus = itemView.findViewById(R.id.status);
            tvTotalPrice = itemView.findViewById(R.id.total_price);
            imageView = itemView.findViewById(R.id.imageView9);
            recyclerViewItemOrder = itemView.findViewById(R.id.recyclerViewItemOrder);
            btnBuy = itemView.findViewById(R.id.btn_buy);
            btnDetail = itemView.findViewById(R.id.btn_detail);
        }
    }

    private void cancelOrder(OrderStoreViewHolder holder, Order order) {
        int position = holder.getAdapterPosition(); // Lấy đúng vị trí của item
        if (position == RecyclerView.NO_POSITION) {
            return; // Nếu vị trí không hợp lệ, thoát khỏi phương thức
        }

        if (order.getStatus().equals("pending") && order.getPaymentMethod().equals("COD")) {
            viewModel.updateOrderStatus(order.getOrderId(), "cancel", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                @Override
                public void onSuccess(String message) {
                    // Cập nhật trạng thái của item trong adapter
                    order.setStatus("cancel");
                    notifyItemChanged(position); // Chỉ cập nhật item tại vị trí hiện tại
                    Toast.makeText(holder.itemView.getContext(), "Đơn hàng đã hủy!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(holder.itemView.getContext(), "Không thể hủy đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (order.getStatus().equals("approved")) {
            viewModel.updateOrderStatus(order.getOrderId(), "delivered", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                @Override
                public void onSuccess(String message) {
                    // Cập nhật trạng thái của item trong adapter
                    order.setStatus("delivered");
                    notifyItemChanged(position);

                    Intent intent = new Intent(holder.itemView.getContext(), ProductRatingActivity.class);
                    intent.putExtra("order", order);
                    holder.itemView.getContext().startActivity(intent);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(holder.itemView.getContext(), "Không thể cập nhật trạng thái: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (order.getStatus().equals("delivered") && !order.isCheckRating()) {

            // Cập nhật trạng thái của item trong adapter
            order.setStatus("delivered");
            notifyItemChanged(position);

            Intent intent = new Intent(holder.itemView.getContext(), ProductRatingActivity.class);
            intent.putExtra("order", order);
            holder.itemView.getContext().startActivity(intent);


        }
    }


}
