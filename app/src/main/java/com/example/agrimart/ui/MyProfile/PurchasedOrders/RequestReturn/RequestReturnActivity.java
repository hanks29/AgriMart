package com.example.agrimart.ui.MyProfile.PurchasedOrders.RequestReturn;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ProductOrderAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.Payment.VnpayRefund;
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestReturnActivity extends AppCompatActivity {
    RecyclerView recyclerViewDetail;
    Order order;
    TextView totalPrice, reasonText;
    ImageButton btnBack;
    AppCompatButton btnGui;
    private OrderStatusFragmentViewModel viewModel;
    LinearLayout myReason;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_return);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        order = (Order) getIntent().getSerializableExtra("order");
        addControl();
        loadDetail();
        addEvent();
    }


    void addControl()
    {
        recyclerViewDetail = findViewById(R.id.recyclerViewDetail);
        totalPrice = findViewById(R.id.total_price);
        btnBack = findViewById(R.id.btn_back);
        btnGui = findViewById(R.id.btn_gui);
        myReason = findViewById(R.id.my_reason);
        reasonText = findViewById(R.id.reason_text);

        viewModel = new OrderStatusFragmentViewModel();
    }

    @SuppressLint("SetTextI18n")
    void loadDetail() {
        List<Product> products = order.getProducts();
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products);
        recyclerViewDetail.setAdapter(productOrderAdapter);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));

        totalPrice.setText(formatCurrency(order.getTotalPrice()) + " đ");
    }

    private void addEvent() {
        btnBack.setOnClickListener(v -> finish());
        btnGui.setOnClickListener(v-> requestReturn());
        myReason.setOnClickListener(v -> showReasonDialog((String) reasonText.getText()));
    }

    private void showReasonDialog(String s) {
        // Tạo BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_reason);

        // Tham chiếu đến các view trong dialog
        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.reason_radio_group);
        AppCompatButton btnSubmit = bottomSheetDialog.findViewById(R.id.btn_submit_reason);

        // Tự động chọn lý do nếu chuỗi `s` được truyền vào
        if (s != null && !s.isEmpty()) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                if (radioButton.getText().toString().equalsIgnoreCase(s)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }

        btnSubmit.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedReason = bottomSheetDialog.findViewById(selectedId);
                String reason = selectedReason.getText().toString();
                reasonText.setText(reason);

                // Đóng dialog
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng chọn lý do", Toast.LENGTH_SHORT).show();
            }
        });

        // Hiển thị dialog
        bottomSheetDialog.show();
    }



    private void requestReturn() {
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
                                Toast.makeText(this, "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                            });

                            // Cập nhật trạng thái đơn hàng
                            viewModel.updateOrderStatusRefund(order.getOrderId(), order.getStatus(), new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
                                @Override
                                public void onSuccess(String message) {
                                    order.setStatus(order.getStatus());
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                        Toast.makeText(RequestReturnActivity.this, "Không thể hủy đơn hàng: " , Toast.LENGTH_SHORT).show();
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
    
    private String formatCurrency(double amount) {
        return NumberFormat.getInstance(Locale.getDefault()).format(amount);
    }
}