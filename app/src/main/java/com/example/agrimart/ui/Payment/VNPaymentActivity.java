package com.example.agrimart.ui.Payment;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.data.API.Config_VNPAY;
import com.example.agrimart.data.model.Address;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.Cart.PlaceOrderActivity;
import com.example.agrimart.ui.MainActivity;
import com.example.agrimart.utils.VnpayUtils;
import com.example.agrimart.viewmodel.CheckoutViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VNPaymentActivity extends AppCompatActivity {

    private WebView webView;
    private CheckoutViewModel checkoutViewModel;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vn_payment);

        int price = getIntent().getIntExtra("price", 0);
        String orderInfo = getIntent().getStringExtra("orderInfo");
        String address = getIntent().getStringExtra("address");
        String storeId = getIntent().getStringExtra("storeId");
        String username = getIntent().getStringExtra("username");
        String phonenumber = getIntent().getStringExtra("phonenumber");
        ArrayList<Product> products = getIntent().getParcelableArrayListExtra("products");

        Address addr = new Address();
        addr.setStreet(address);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        db = FirebaseFirestore.getInstance();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains(Config_VNPAY.VNP_RETURN_URL)) {
                    if (url.contains("vnp_ResponseCode=00")) {
                        Toast.makeText(VNPaymentActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();

                        List<String> productIds = getIntent().getStringArrayListExtra("productIds");
                        List<Address> addresses = new ArrayList<>();
                        addresses.add(addr);

                        String transactionId = extractTransactionId(url);
                        String vnpTxnRef = extractVnpTxnRef(url);
                        Timestamp transactionDate = new Timestamp(new Date().getTime());

                        checkoutViewModel.placeOrder(price, "3-5 ngày", 0, "VNPay", "Giao hàng nhanh", productIds, address, storeId, products, username, phonenumber, new CheckoutViewModel.OrderCallback() {
                            @Override
                            public void onSuccess(String orderId) {
                                db.collection("orders").document(orderId)
                                        .update("transactionId", transactionId, "transactionDate", transactionDate, "vnpTxnRef", vnpTxnRef)
                                        .addOnSuccessListener(aVoid -> {
                                            Intent intent = new Intent(VNPaymentActivity.this, PlaceOrderActivity.class);
                                            intent.putExtra("orderId", orderId);
                                            intent.putExtra("orderInfo", orderInfo);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(VNPaymentActivity.this, "Lưu thông tin giao dịch thất bại", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(VNPaymentActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (url.contains("vnp_ResponseCode=24")) {
                        Toast.makeText(VNPaymentActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VNPaymentActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(VNPaymentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        try {
            String paymentUrl = VnpayUtils.createVnpayUrl(
                    price,
                    orderInfo,
                    null,
                    "vn"
            );

            webView.loadUrl(paymentUrl);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tạo URL thanh toán", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractTransactionId(String url) {
        String prefix = "vnp_TransactionNo=";
        int startIndex = url.indexOf(prefix) + prefix.length();
        int endIndex = url.indexOf("&", startIndex);
        if (endIndex == -1) {
            endIndex = url.length();
        }
        return url.substring(startIndex, endIndex);
    }

    private String extractVnpTxnRef(String url) {
        String prefix = "vnp_TxnRef=";
        int startIndex = url.indexOf(prefix) + prefix.length();
        int endIndex = url.indexOf("&", startIndex);
        if (endIndex == -1) {
            endIndex = url.length();
        }
        return url.substring(startIndex, endIndex);
    }
}