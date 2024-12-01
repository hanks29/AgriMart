package com.example.agrimart.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.Cart.CheckoutActivity;
import com.example.agrimart.ui.MyProfile.MyRating.ProductRatingActivity;
import com.example.agrimart.ui.MyProfile.MyRating.ShopRatingActivity;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.OrderInformationActivity;
import com.example.agrimart.ui.Payment.VnpayRefund;
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderStoreAdapter extends RecyclerView.Adapter<OrderStoreAdapter.OrderStoreViewHolder> {
    private final List<Order> orderStoreList = new ArrayList<>();
    private OrderStatusFragmentViewModel viewModel;
    private final int REQUEST_CODE_RATING = 1001;


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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderStoreViewHolder holder, int position) {
        // Get the current OrderStore
        Order orderStore = orderStoreList.get(position);

        List<Product> products = orderStore.getProducts();
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products);
        productOrderAdapter.setOnProductClickListener(product -> {
            openDetail(holder, orderStore);
        });

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
                translatedStatus = "Chờ lấy hàng";
                break;
            case "delivery":
                translatedStatus = "Chờ giao hàng";
                holder.btnBuy.setText("Đã nhận hàng");
                holder.btnDetail.setVisibility(View.VISIBLE);
                holder.btnDetail.setText("Trả hàng/Hoàn tiền");
                break;
            case "refund" :
                translatedStatus = "Chờ giao hàng";
                holder.btnBuy.setText("Đã nhận hàng");
                break;
            case "delivered":
                translatedStatus = "Hoàn thành";
                if (!orderStore.isCheckRating()) {
                    holder.btnBuy.setText("Đánh giá");
                    holder.btnDetail.setVisibility(View.VISIBLE);
                    holder.btnDetail.setText("Trả hàng/Hoàn tiền");
                }else {
                    holder.btnDetail.setVisibility(View.VISIBLE);
                }

                break;
            case "canceled":
                translatedStatus = "Đã hủy";
                holder.btnDetail.setText("Xem Thông tin Hoàn tiền");
                break;
            default:
                translatedStatus = "Không xác định";
                break;
        }
        holder.tvStatus.setText(translatedStatus);

        // Thêm listener cho btnBuy
        holder.btnBuy.setOnClickListener(v -> cancelOrder(holder, orderStore));  // Truyền đúng item vào đây

        holder.tvTotalPrice.setText("Tổng số tiền: " + orderStore.getTotalPrice() + " VND");

        holder.main.setOnClickListener(v -> openDetail(holder, orderStore));

        holder.btnDetail.setOnClickListener(v -> openRating(holder, orderStore));

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
        LinearLayout main;

        public OrderStoreViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main);
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
            viewModel.updateOrderStatus(order.getOrderId(), "canceled", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                @Override
                public void onSuccess(String message) {
                    // Cập nhật trạng thái của item trong adapter
                    order.setStatus("pending");
                    notifyItemChanged(position); // Chỉ cập nhật item tại vị trí hiện tại
                    Toast.makeText(holder.itemView.getContext(), "Đơn hàng đã hủy!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(holder.itemView.getContext(), "Không thể hủy đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (order.getStatus().equals("pending") && order.getPaymentMethod().equals("VNPay")) {
            // Khi trả hàng gọi api hoàn tiền
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("orders").document(order.getOrderId()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String vnpTxnRef = documentSnapshot.getString("vnpTxnRef");
                    order.setVnpTxnRef(vnpTxnRef);

                    new Thread(() -> {
                        try {
                            String vnp_TxnRef = order.getVnpTxnRef();
                            String transactionId = order.getTransactionId();
                            int totalPrice = order.getTotalPrice();
                            String formattedTransactionDate = formatTimestampToVnpayDate(order.getTransactionDate());

                            // Gửi yêu cầu hoàn tiền
                            String response = VnpayRefund.createRefundRequest(
                                    vnp_TxnRef,          // Mã giao dịch của merchant (txnRef)
                                    transactionId,       // Mã giao dịch từ VNPAY
                                    totalPrice,          // Số tiền hoàn
                                    formattedTransactionDate, // Ngày giao dịch gốc
                                    "Hoàn tiền cho đơn hàng " + order.getOrderId(), // Lý do hoàn tiền
                                    "admin"              // Người thực hiện
                            );

                            //nếu hoàn tiền thành công
                            if (response.contains("\"vnp_ResponseCode\":\"00\"")) { //ResponseCode là 00 (Hoàn tiền thành công)
                                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(holder.itemView.getContext(), "Huỷ đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                });

                                // Cập nhật trạng thái đơn hàng
                                viewModel.updateOrderStatus(order.getOrderId(), "canceled", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                                    @Override
                                    public void onSuccess(String message) {
                                        order.setStatus("canceled");
                                        notifyItemChanged(position);
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                            Toast.makeText(holder.itemView.getContext(), "Không thể hủy đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                });

                            } else {
                                //nếu hoàn tiền không thành công
                                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(holder.itemView.getContext(), "Không thể hoàn tiền: " + response, Toast.LENGTH_SHORT).show();
                                });
                                Log.println(Log.ERROR, "VnpayRefund", response);
                            }
                        } catch (Exception e) {
                            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(holder.itemView.getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(holder.itemView.getContext(), "Lỗi khi lấy thông tin đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else if (order.getStatus().equals("delivery")) {
            viewModel.updateOrderStatus(order.getOrderId(), "delivered", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                @Override
                public void onSuccess(String message) {
                    // Cập nhật trạng thái của item trong adapter
                    order.setStatus("delivery");
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
            intent.putExtra("position", position);
            ((Activity) holder.itemView.getContext()).startActivityForResult(intent, REQUEST_CODE_RATING);
        } else {
            onCheckoutButtonClicked(holder, order);
        }
    }

    private void openDetail(OrderStoreViewHolder holder, Order order) {
        int position = holder.getAdapterPosition(); // Lấy đúng vị trí của item
        if (position == RecyclerView.NO_POSITION) {
            return; // Nếu vị trí không hợp lệ, thoát khỏi phương thức
        }

        Intent intent = new Intent(holder.itemView.getContext(), OrderInformationActivity.class);
        intent.putExtra("order", order);
        holder.itemView.getContext().startActivity(intent);
    }

    private void onCheckoutButtonClicked(OrderStoreViewHolder holder, Order order) {
        List<Product> selectedProducts = new ArrayList<>();
        List<String> productIds = new ArrayList<>();

        selectedProducts = order.getProducts();

        for (Product p : selectedProducts)
        {
            productIds.add(p.getProduct_id());
        }

        Intent intent = new Intent(holder.itemView.getContext(), CheckoutActivity.class);
        intent.putParcelableArrayListExtra("selectedProducts", new ArrayList<>(selectedProducts));
        intent.putStringArrayListExtra("productIds", new ArrayList<>(productIds));
        intent.putExtra("storeName", order.getStoreName());
        holder.itemView.getContext().startActivity(intent);

    }

    private void openRating(OrderStoreViewHolder holder, Order order) {
        Intent intent = new Intent(holder.itemView.getContext(), ShopRatingActivity.class);
        intent.putExtra("order", order);
        holder.itemView.getContext().startActivity(intent);
    }

    public static String formatTimestampToVnpayDate(Timestamp timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date(timestamp.toDate().getTime());
        return formatter.format(date);
    }

}
