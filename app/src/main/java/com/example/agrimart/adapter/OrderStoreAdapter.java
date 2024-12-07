package com.example.agrimart.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.agrimart.ui.MyProfile.PurchasedOrders.RequestReturn.RequestReturnActivity;
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
    String translatedStatus;


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
        Order order = orderStoreList.get(position);

        List<Product> products = order.getProducts();
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products);
        productOrderAdapter.setOnProductClickListener(product -> {
            openDetail(holder, order);
        });

        holder.recyclerViewItemOrder.setAdapter(productOrderAdapter);
        holder.recyclerViewItemOrder.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        // Set text data
        holder.tvStoreName.setText(order.getStoreName());

        setStatusButton(holder, order);

        holder.tvStatus.setText(translatedStatus);


        holder.tvTotalPrice.setText("Tổng số tiền: " + order.getTotalPrice() + " VND");

        holder.main.setOnClickListener(v -> openDetail(holder, order));

        holder.btnBuy.setOnClickListener(v -> {

            if (order.getStatus().equals("pending")
                    && order.getPaymentMethod().equals("COD")) {
                cancelOrderCOD(holder, order, position);
            } else if (order.getStatus().equals("pending")
                    && order.getPaymentMethod().equals("VNPay")) {
                cancelOrderVNPay(holder, order, position);
            } else if (order.getStatus().equals("delivering")) {
                openRatingDelivering(holder, order, position);
            } else if (order.getStatus().equals("delivered") && !order.isCheckRating()) {
                openRatingDelivered(holder, order, position);
            } else {
                onCheckoutButtonClicked(holder, order);
            }

        });

        holder.btnDetail.setOnClickListener(v -> {
            if (!order.isCheckRating()
                    && (order.getStatus().equals("delivering")
                    || order.getStatus().equals("delivered"))
                    && order.getPaymentMethod().equals("VNPay")) {
                if (!order.checkTime()) {
                    // Hiển thị dialog thông báo
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Thông báo")
                            .setMessage("Đã quá 6 tiếng! Không thể thực hiện yêu cầu.")
                            .setPositiveButton("Đồng ý", (dialog, which) -> {
                                // Ẩn các thành phần
                                holder.btnDetail.setVisibility(View.GONE);
                                holder.txtThongBao.setVisibility(View.GONE);
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    openRequestReturn(holder, order);
                }
            } else {
                openRatingList(holder, order);
            }
        });


    }


    @Override
    public int getItemCount() {
        return orderStoreList.size();
    }

    // ViewHolder class
    static class OrderStoreViewHolder extends RecyclerView.ViewHolder {

        TextView tvStoreName, tvStatus, tvTotalPrice, txtThongBao;
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
            txtThongBao = itemView.findViewById(R.id.txt_ThongBao);
        }
    }

    private void setStatusButton(OrderStoreViewHolder holder, Order order) {
        switch (order.getStatus()) {
            case "pending":
                translatedStatus = "Chờ xác nhận";
                holder.btnBuy.setText("Hủy đơn hàng");
                break;
            case "approved":
                translatedStatus = "Shop đang chuẩn bị hàng";
                holder.btnBuy.setVisibility(View.GONE);
                break;
            case "delivering":
                translatedStatus = "Chờ giao hàng";
                holder.btnBuy.setText("Đã nhận hàng");
                if (order.checkTime())
                {
                    holder.txtThongBao.setVisibility(View.VISIBLE);
                    holder.btnDetail.setVisibility(View.VISIBLE);
                    holder.btnDetail.setText("Trả hàng/Hoàn tiền");
                }

                break;
            case "return":
                translatedStatus = "Chờ giao hàng";
                holder.btnBuy.setText("Đã nhận hàng");
                break;
            case "delivered":
                translatedStatus = "Hoàn thành";
                if (order.isCheckRating()) {
                    holder.btnDetail.setVisibility(View.VISIBLE);
                } else {
                    if (!order.isCheckRating() && order.getPaymentMethod().equals("VNPay")) {
                        holder.btnBuy.setText("Đánh giá");
                        if (order.checkTime())
                        {
                            holder.txtThongBao.setVisibility(View.VISIBLE);
                            holder.btnDetail.setVisibility(View.VISIBLE);
                            holder.btnDetail.setText("Trả hàng/Hoàn tiền");
                        }
                    } else {
                        holder.btnBuy.setText("Đánh giá");
                    }
                }
                break;
            case "canceled":
                if (order.isRefund()) {
                    translatedStatus = "Đã hủy và Hoàn tiền";
                } else {
                    translatedStatus = "Đã hủy";
                }
                break;
            default:
                translatedStatus = "Không xác định";
                break;
        }
    }

    private void cancelOrderCOD(OrderStoreViewHolder holder, Order order, int position) {
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
    }

    private void cancelOrderVNPay(OrderStoreViewHolder holder, Order order, int position) {
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
                        String formattedTransactionDate = formatTimestampToVnpayDate(order.getTransactionDateMillis());

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
                            viewModel.updateOrderStatusRefund(order.getOrderId(), "canceled", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
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
                            Log.println(Log.ERROR, "Vnpayreturn", response);
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
    }

    private void openRatingDelivering(OrderStoreViewHolder holder, Order order, int position) {
        viewModel.updateOrderStatus(order.getOrderId(), "delivered", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
            @Override
            public void onSuccess(String message) {
                // Cập nhật trạng thái của item trong adapter
                viewModel.getData("delivering");
                order.setStatus("delivering");
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
    }

    private void openRatingDelivered(OrderStoreViewHolder holder, Order order, int position) {
        order.setStatus("delivered");
        notifyItemChanged(position);

        Intent intent = new Intent(holder.itemView.getContext(), ProductRatingActivity.class);
        intent.putExtra("order", order);
        ((Activity) holder.itemView.getContext()).startActivityForResult(intent, REQUEST_CODE_RATING);
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

        for (Product p : selectedProducts) {
            productIds.add(p.getProduct_id());
        }

        Intent intent = new Intent(holder.itemView.getContext(), CheckoutActivity.class);
        intent.putParcelableArrayListExtra("selectedProducts", new ArrayList<>(selectedProducts));
        intent.putStringArrayListExtra("productIds", new ArrayList<>(productIds));
        intent.putExtra("storeName", order.getStoreName());
        holder.itemView.getContext().startActivity(intent);

    }

    private void openRatingList(OrderStoreViewHolder holder, Order order) {
        Intent intent = new Intent(holder.itemView.getContext(), ShopRatingActivity.class);
        intent.putExtra("order", order);
        holder.itemView.getContext().startActivity(intent);
    }

    private void openRequestReturn(OrderStoreViewHolder holder, Order order) {
        Intent intent = new Intent(holder.itemView.getContext(), RequestReturnActivity.class);
        intent.putExtra("order", order);
        holder.itemView.getContext().startActivity(intent);
    }

    public static String formatTimestampToVnpayDate(Long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date(timestamp);
        return formatter.format(date);
    }

}
