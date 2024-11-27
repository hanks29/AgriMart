package com.example.agrimart.ui.MyProfile.PurchasedOrders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ProductOrderAdapter;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.Cart.CheckoutActivity;
import com.google.firebase.Timestamp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderInformationActivity extends AppCompatActivity {
    Order order;
    TextView status, shippingName, address, totalPriceProduct, shippingFee, totalPrice;
    AppCompatButton btnBuy, btnDetail;
    ImageButton btnBack;
    RecyclerView recyclerViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);

        // Initialize order object from intent
        order = (Order) getIntent().getSerializableExtra("order");

        // Add controls
        addControl();

        // Load order details
        loadDetail();

        // Set events
        addEvent();
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
    }

    @SuppressLint("SetTextI18n")
    void loadDetail() {
        shippingName.setText(order.getShippingName());
        String userDetail = order.getUsername() + " " + order.getPhonenumber();
        address.setText(userDetail + "\n" + order.getAddress());

        // Format shipping fee and total price
        shippingFee.setText(formatCurrency(order.getShippingFee()) + " đ");
        totalPrice.setText("Tổng số tiền: " + formatCurrency(order.getTotalPrice()) + " đ");

        // Calculate total product price (excluding shipping fee)
        double priceProduct = order.getTotalPrice() - order.getShippingFee();
        totalPriceProduct.setText(formatCurrency(priceProduct) + " đ");

        // Format the createdAt date
        status.setText(getStatus(order.getStatus()) + order.getFormattedCreatedAtDate());

        // Set up RecyclerView for product details
        List<Product> products = order.getProducts();
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products);
        recyclerViewDetail.setAdapter(productOrderAdapter);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));
    }

    void addEvent() {
        btnBack.setOnClickListener(v -> finish());
        btnBuy.setOnClickListener(v -> onCheckoutButtonClicked());
    }

    String getStatus(String status) {

        switch (status) {
            case "pending":
                return "Chờ xác nhận ";
            case "approved":
                return "Chờ giao hàng ";
            case "delivered":
                btnDetail.setVisibility(View.VISIBLE);
                return "Đã giao vào ";
            case "canceled":
                return "Đã hủy vào ";
        }

        return "Không xác định";

    }

    // Helper method to format currency
    private String formatCurrency(double amount) {
        return NumberFormat.getInstance(Locale.getDefault()).format(amount);
    }


    private void onCheckoutButtonClicked() {
        List<Product> selectedProducts = new ArrayList<>();
        List<String> productIds = new ArrayList<>();

        selectedProducts = order.getProducts();

        for (Product p : selectedProducts)
        {
            productIds.add(p.getProduct_id());
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putParcelableArrayListExtra("selectedProducts", new ArrayList<>(selectedProducts));
        intent.putStringArrayListExtra("productIds", new ArrayList<>(productIds));
        intent.putExtra("storeName", order.getStoreName());
        startActivity(intent);

    }
}
