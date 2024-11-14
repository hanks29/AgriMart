package com.example.agrimart.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agrimart.R;
import com.example.agrimart.data.API.Config_VNPAY;
import com.example.agrimart.ui.MainActivity;
import com.example.agrimart.utils.VnpayUtils;

public class VNPaymentActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vn_payment);

        int amount = getIntent().getIntExtra("amount", 0);
        String orderInfo = getIntent().getStringExtra("orderInfo");

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.contains(Config_VNPAY.VNP_RETURN_URL)) {
                    if (url.contains("vnp_ResponseCode=00")) {
                        Toast.makeText(VNPaymentActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
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
                    amount,
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
}
