package com.example.agrimart.ui.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.CheckoutAdapter;
import com.example.agrimart.data.model.Address;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.MyProfile.MyAddress.MyAddressActivity;
import com.example.agrimart.ui.Payment.VNPaymentActivity;
import com.example.agrimart.viewmodel.CheckoutViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import android.app.AlertDialog;
import android.widget.Toast;

public class CheckoutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvProduct;
    private CheckoutAdapter checkoutAdapter;
    private List<Product> selectedProducts;
    private TextView tvTotalProductPrice, tvTotalShippingPrice, tvFinalTotalPrice, tvTotalPrice;
    private TextView tvUserName, tvPhoneNumber, tvAddress, tvChangeAddress;
    private Button btnCheckout, btnPlaceOrder;
    private RadioGroup paymentMethodGroup;
    private RadioButton radVNPay, radCOD;
    private CheckoutViewModel checkoutViewModel;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        }

        toolbar = findViewById(R.id.header);
        toolbar.setNavigationOnClickListener(v -> finish());

        selectedProducts = getIntent().getParcelableArrayListExtra("selectedProducts");
        if (selectedProducts == null) {
            selectedProducts = new ArrayList<>();
        }

        rvProduct = findViewById(R.id.rv_product);
        rvProduct.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutAdapter(selectedProducts);
        rvProduct.setAdapter(checkoutAdapter);

        tvTotalProductPrice = findViewById(R.id.tvTotalProductPrice);
        tvTotalShippingPrice = findViewById(R.id.tvTotalShippingPrice);
        tvFinalTotalPrice = findViewById(R.id.tvFinalTotalPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvUserName = findViewById(R.id.tvUserName1);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber1);
        tvAddress = findViewById(R.id.tvAddress1);
        tvChangeAddress = findViewById(R.id.tvChangeAddress);
        paymentMethodGroup = findViewById(R.id.radGroupPayment);
        radVNPay = findViewById(R.id.radVNPay);
        radCOD = findViewById(R.id.radCOD);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        tvChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, MyAddressActivity.class);
            startActivity(intent);
        });

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        checkoutViewModel.loadUserData(tvUserName, tvPhoneNumber, tvAddress);

        updatePrices();

        orderId = checkoutViewModel.generateOrderId();

        paymentMethodGroup.setOnCheckedChangeListener((group, checkId) -> {
            if (checkId == R.id.radVNPay) {
                //radCOD.setChecked(false);
            } else if (checkId == R.id.radCOD) {
                //radVNPay.setChecked(false);
            }
        });

        paymentMethodGroup.check(R.id.radCOD);

        paymentMethodGroup.clearCheck();

        btnPlaceOrder.setOnClickListener(v -> { placeOrder();});
    }

    private void placeOrder() {
        List<Address> addresses = new ArrayList<>();

        if (radVNPay.isChecked()) {
            radCOD.setChecked(false);
            int amount = Integer.parseInt(tvTotalPrice.getText().toString().replaceAll("[^0-9]", ""));
            String orderInfo = "Thanh toán đơn hàng " + orderId;
            List<String> productIds = selectedProducts.stream()
                    .map(Product::getProduct_id)
                    .collect(Collectors.toList());
            Intent intent = new Intent(CheckoutActivity.this, VNPaymentActivity.class);
            intent.putExtra("price", amount);
            intent.putExtra("orderInfo", orderInfo);
            intent.putStringArrayListExtra("productIds", new ArrayList<>(productIds));
            intent.putParcelableArrayListExtra("addresses", new ArrayList<Address>(addresses));
            startActivity(intent);
        } else if (radCOD.isChecked()) {
            radVNPay.setChecked(false);
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận đặt hàng")
                    .setMessage("Bạn có chắc chắn muốn đặt hàng?")
                    .setPositiveButton("Đặt hàng", (dialog, which) -> {
                        double totalPrice = Double.parseDouble(tvTotalPrice.getText().toString().replaceAll("[^0-9]", ""));
                        String expectedDeliveryTime = "3-5 ngày";
                        double shippingFee = 0;
                        String paymentMethod = "COD";
                        String shippingName = "Giao hàng nhanh";

                        List<String> productIds = selectedProducts.stream()
                                .map(Product::getProduct_id)
                                .collect(Collectors.toList());

                        checkoutViewModel.placeOrder(totalPrice, expectedDeliveryTime, shippingFee, paymentMethod, shippingName, productIds, addresses, new CheckoutViewModel.OrderCallback() {
                            @Override
                            public void onSuccess(String orderId) {
                                checkoutViewModel.removeOrderedProductsFromCart(FirebaseAuth.getInstance().getCurrentUser().getUid(), new CheckoutViewModel.OrderCallback() {
                                    @Override
                                    public void onSuccess(String orderId) {
                                        Intent intent = new Intent(CheckoutActivity.this, PlaceOrderActivity.class);
                                        intent.putExtra("orderId", orderId);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }, orderId);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                    })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Chọn phương thức thanh toán")
                    .setMessage("Vui lòng chọn phương thức thanh toán")
                    .setPositiveButton("OK", (dialog, which) -> {
                    })
                    .show();
        }
    }

    private void showNoAddressDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Không có địa chỉ nhận hàng")
                .setMessage("Vui lòng thêm địa chỉ nhận hàng")
                .setPositiveButton("Thêm địa chỉ", (dialog, which) -> {
                    Intent intent = new Intent(CheckoutActivity.this, MyAddressActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Thoát", (dialog, which) -> finish())
                .show();
    }

    private void updatePrices() {
        double totalProductPrice = 0;
        for (Product product : selectedProducts) {
            totalProductPrice += product.getPrice() * product.getQuantity();
        }

        double totalShippingPrice = 0;
        double finalTotalPrice = totalProductPrice + totalShippingPrice;

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalProductPrice.setText(format.format(totalProductPrice));
        tvTotalShippingPrice.setText(format.format(totalShippingPrice));
        tvFinalTotalPrice.setText(format.format(finalTotalPrice));
        tvTotalPrice.setText(format.format(finalTotalPrice));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            checkoutViewModel.loadUserData(tvUserName, tvPhoneNumber, tvAddress);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkoutViewModel.loadUserData(tvUserName, tvPhoneNumber, tvAddress);
        checkoutViewModel.isUserDataAvailable(isAvailable -> {
            if (!isAvailable) {
                showNoAddressDialog();
            }
        });
    }
}