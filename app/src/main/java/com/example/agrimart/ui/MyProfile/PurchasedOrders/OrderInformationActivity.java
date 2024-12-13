package com.example.agrimart.ui.MyProfile.PurchasedOrders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.OrderStoreAdapter;
import com.example.agrimart.adapter.ProductOrderAdapter;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.Cart.CheckoutActivity;
import com.example.agrimart.ui.MyProfile.MyRating.ProductRatingActivity;
import com.example.agrimart.ui.MyProfile.MyRating.ShopRatingActivity;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.RequestReturn.RequestReturnActivity;
import com.example.agrimart.ui.Payment.VnpayRefund;
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderInformationActivity extends AppCompatActivity {
    Order order;
    TextView status, shippingName, address, totalPriceProduct, shippingFee, totalPrice, tvRefund;
    AppCompatButton btnBuy, btnDetail;
    ImageButton btnBack;
    RecyclerView recyclerViewDetail;
    LinearLayout llRefund;
    private final int REQUEST_CODE_RATING = 1001;
    private OrderStatusFragmentViewModel viewModel;
    LinearLayout footer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);

        // Initialize order object from intent
        order = (Order) getIntent().getSerializableExtra("order");
        viewModel = new OrderStatusFragmentViewModel();

        // Add controls
        addControl();

        // Load order details
        loadDetail();

        // Set events
        addEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RATING) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    void addControl() {
        status = findViewById(R.id.status);
        shippingName = findViewById(R.id.shipping_name);
        address = findViewById(R.id.address);
        totalPriceProduct = findViewById(R.id.total_price_product);
        shippingFee = findViewById(R.id.shipping_fee);
        totalPrice = findViewById(R.id.total_price);
        btnBuy = findViewById(R.id.btn_buy);
        btnBack = findViewById(R.id.btn_back);
        btnDetail = findViewById(R.id.btn_detail);
        recyclerViewDetail = findViewById(R.id.recyclerViewDetail);
        llRefund = findViewById(R.id.ll_refund);
        tvRefund = findViewById(R.id.tv_refund);
        footer = findViewById(R.id.footer);

    }

    @SuppressLint("SetTextI18n")
    void loadDetail() {
        shippingName.setText(order.getShippingName());
        String userDetail = order.getUsername() + " " + order.getPhonenumber();
        address.setText(userDetail + "\n" + order.getAddress());

        // phí vận chuyển và tổng tiền
        shippingFee.setText(formatCurrency(order.getShippingFee()) + " đ");
        totalPrice.setText("Tổng số tiền: " + formatCurrency(order.getTotalPrice()) + " đ");

        // tính tổng tiền không gồm phí vận chuyển
        double priceProduct = order.getTotalPrice() - order.getShippingFee();
        totalPriceProduct.setText(formatCurrency(priceProduct) + " đ");

        tvRefund.setText(formatCurrency(order.getTotalPrice()) + " đã được hoàn về tài khoản VNpay của bạn");

        // Format lại ngày
        status.setText(getStatus(order.getStatus()) + order.getFormattedCreatedAtDate());

        // RecyclerView để biết chi tiết sản phẩm
        List<Product> products = order.getProducts();
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products);
        recyclerViewDetail.setAdapter(productOrderAdapter);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));
    }

    void addEvent() {
        btnBack.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("order_status", order.getStatus());
            setResult(RESULT_OK, resultIntent);
            finish(); // cập nhật lại trạng thái sản phẩm mỗi khi quay lại
        });
        btnBuy.setOnClickListener(v -> {
            if (order.getStatus().equals("pending")) {
                if(order.getPaymentMethod().equals("COD"))
                {
                    cancelOrderCOD(); // mở huỷ hàng khi chọn thanh toán sau khi nhận
                } else {
                    cancelOrderVNPay(); // mở huỷ hàng/hoàn tiền khi chọn thanh toán bằng VNPay
                }
                
            } else if (order.isCheckRating()) {
                onCheckoutButtonClicked(); // mở mua lại khi đã đánh giá
            } else {
                openRating();// mở đánh giá khi chưa đánh giá
            }

        });
        btnDetail.setOnClickListener(v -> {
            if (order.isCheckRating()) {
                openRatingDetail();
            } else {
                if (!order.checkTime()) {
                    // Hiển thị dialog thông báo
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Thông báo")
                            .setMessage("Đã quá 6 tiếng! Không thể thực hiện yêu cầu.")
                            .setPositiveButton("Đồng ý", (dialog, which) -> {
                                btnDetail.setVisibility(View.GONE);
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    openRequestReturn();
                }

            }
        });
    }

    @SuppressLint("SetTextI18n")
    String getStatus(String status) {

        switch (status) {
            case "pending":
                btnBuy.setText("Huỷ đơn hàng");
                return "Chờ xác nhận ";
            case "approved":
                footer.setVisibility(View.GONE);
                return "Shop đang chuẩn bị hàng ";
            case "delivering":
                btnBuy.setText("Đã nhận hàng");
                if (order.checkTime() && order.getPaymentMethod().equals("VNPay")) {
                    btnDetail.setVisibility(View.VISIBLE);
                    btnDetail.setText("Trả hàng/Hoàn tiền");
                }
                return "Chờ giao hàng ";
            case "return":
                footer.setVisibility(View.GONE);
                if (!order.isRefund()) {
                    return "Chờ hoàn tiền ";
                } else {
                    llRefund.setVisibility(View.VISIBLE);
                }
                return "Đã trả hoàn tiền ";
            case "delivered":
                if (!order.isCheckRating()) {
                    btnBuy.setText("Đánh giá");
                    if (order.getPaymentMethod().equals("VNPay") && order.checkTime()) {
                        btnDetail.setVisibility(View.VISIBLE);
                        btnDetail.setText("Trả hàng/Hoàn tiền");
                    }
                } else {
                    btnDetail.setVisibility(View.VISIBLE);
                    btnBuy.setText("Mua lại");
                }

                return "Đã giao vào ";
            case "canceled":
                if (order.isRefund()) {

                    llRefund.setVisibility(View.VISIBLE);
                }

                return "Đã hủy vào ";
        }

        return "Không xác định";

    }


    private String formatCurrency(double amount) {
        return NumberFormat.getInstance(Locale.getDefault()).format(amount);
    }

    private void onCheckoutButtonClicked() {
        List<Product> selectedProducts = new ArrayList<>();
        List<String> productIds = new ArrayList<>();

        selectedProducts = order.getProducts();

        for (Product p : selectedProducts) {
            productIds.add(p.getProduct_id());
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putParcelableArrayListExtra("selectedProducts", new ArrayList<>(selectedProducts));
        intent.putStringArrayListExtra("productIds", new ArrayList<>(productIds));
        intent.putExtra("storeName", order.getStoreName());
        startActivity(intent);
    }

    private void openRating() {
        if (order.getStatus().equals("delivering")) {
            viewModel.updateOrderStatus(order.getOrderId(), "delivered", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                @Override
                public void onSuccess(String message) {
                    // Cập nhật trạng thái của item trong adapter
                    viewModel.getData("delivering");

                    order.setStatus("delivering");

                    Intent intent = new Intent(OrderInformationActivity.this, ProductRatingActivity.class);
                    intent.putExtra("order", order);
                    startActivityForResult(intent, REQUEST_CODE_RATING);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(OrderInformationActivity.this, "Không thể đánh giá: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            order.setStatus("delivered");
            viewModel.getData("delivered");
            Intent intent = new Intent(this, ProductRatingActivity.class);
            intent.putExtra("order", order);
            startActivityForResult(intent, REQUEST_CODE_RATING);
        }

    }

    private void openRatingDetail() {
        Intent intent = new Intent(this, ShopRatingActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }

    private void openRequestReturn() {
        Intent intent = new Intent(this, RequestReturnActivity.class);
        intent.putExtra("order", order);
        startActivityForResult(intent, REQUEST_CODE_RATING);
        if (order.getStatus().equals("delivered")) {
            viewModel.getData("delivered");
        } else {
            viewModel.getData("delivering");
        }
    }

    private void cancelOrderCOD() {
        viewModel.updateOrderStatus(order.getOrderId(), "canceled", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
            @Override
            public void onSuccess(String message) {
                // Cập nhật trạng thái của item trong adapter
                order.setStatus("pending");
                viewModel.getData("pending");
                Toast.makeText(OrderInformationActivity.this, "Đơn hàng đã hủy!", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("order_status", "pending");
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(OrderInformationActivity.this, "Không thể hủy đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrderVNPay() {
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
                                Toast.makeText(this, "Huỷ đơn hàng thành công", Toast.LENGTH_SHORT).show();
                            });

                            // Cập nhật trạng thái đơn hàng
                            viewModel.updateOrderStatusRefund(order.getOrderId(), "canceled", new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                                @Override
                                public void onSuccess(String message) {
                                    order.setStatus("canceled");
                                    viewModel.getData("pending");
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                        Toast.makeText(OrderInformationActivity.this, "Không thể hủy đơn hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });

                        } else {
                            //nếu hoàn tiền không thành công
                            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(this, "Không thể hoàn tiền: " + response, Toast.LENGTH_SHORT).show();
                            });
                            Log.println(Log.ERROR, "Vnpayreturn", response);
                        }
                    } catch (Exception e) {
                        new android.os.Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            } else {
                Toast.makeText(this, "Đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi khi lấy thông tin đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public static String formatTimestampToVnpayDate(Long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date(timestamp);
        return formatter.format(date);
    }
    
}
