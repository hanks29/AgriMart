package com.example.agrimart.ui.MyProfile.PurchasedOrders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;
import com.google.firebase.Timestamp;

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
    int position;

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
    }

    @SuppressLint("SetTextI18n")
    void loadDetail() {
        shippingName.setText(order.getShippingName());
        String userDetail = order.getUsername() + " " + order.getPhonenumber();
        address.setText("Huy Long 0987654321" + "\n" + order.getAddress());

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
        btnBuy.setOnClickListener(v -> {

            if(order.isCheckRating())
            {
                onCheckoutButtonClicked();
            }else {
                openRating();
            }

        });
        btnDetail.setOnClickListener(v -> {
            if(order.isCheckRating())
            {
                openRatingDetail();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    String getStatus(String status) {

        switch (status) {
            case "pending":
                return "Chờ xác nhận ";
            case "approved":
                return "Chờ lấy hàng ";
            case "delivering":
                btnDetail.setVisibility(View.VISIBLE);
                btnDetail.setText("Trả hàng/Hoàn tiền");
                if(!order.isCheckRating())
                {
                    btnDetail.setText("Trả hàng/Hoàn tiền");
                    btnBuy.setText("Đã nhận hàng");
                }
                return "Chờ giao hàng ";

            case "delivered":
                btnDetail.setVisibility(View.VISIBLE);
                if(!order.isCheckRating())
                {
                    btnDetail.setText("Trả hàng/Hoàn tiền");
                    btnBuy.setText("Đánh giá");
                }
                return "Đã giao vào ";
            case "canceled":
                llRefund.setVisibility(View.VISIBLE);
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

                }
            });
        }
        else {
            order.setStatus("delivered");
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

}
