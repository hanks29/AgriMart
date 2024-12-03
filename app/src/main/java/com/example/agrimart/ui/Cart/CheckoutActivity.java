package com.example.agrimart.ui.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.MyProfile.MyAddress.MyAddressActivity;
import com.example.agrimart.ui.Payment.VNPaymentActivity;
import com.example.agrimart.viewmodel.CheckoutViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private Button btnPlaceOrder;
    private RadioGroup paymentMethodGroup;
    private RadioButton radVNPay, radCOD;
    private LinearLayout linearLayout;
    private CheckoutViewModel checkoutViewModel;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        setupWindowInsets();
        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        updatePrices();
        calculateShippingFee();
        createOrderWithGHN();
    }

    private void setupWindowInsets() {
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
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.header);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initializeViews() {
        selectedProducts = getIntent().getParcelableArrayListExtra("selectedProducts");
        if (selectedProducts == null) {
            selectedProducts = new ArrayList<>();
        }

        rvProduct = findViewById(R.id.rv_product);
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
        linearLayout = findViewById(R.id.lnGHN);
    }

    private void setupRecyclerView() {
        rvProduct.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutAdapter(selectedProducts);
        rvProduct.setAdapter(checkoutAdapter);
    }

    private void setupViewModel() {
        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        checkoutViewModel.loadUserData(tvUserName, tvPhoneNumber, tvAddress);
        orderId = checkoutViewModel.generateOrderId();
    }

    private void setupListeners() {
        tvChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, MyAddressActivity.class);
            startActivity(intent);
        });

        paymentMethodGroup.setOnCheckedChangeListener((group, checkId) -> {
            if (checkId == R.id.radVNPay) {
                radCOD.setChecked(false);
            } else if (checkId == R.id.radCOD) {
                radVNPay.setChecked(false);
            }
        });

        btnPlaceOrder.setOnClickListener(v -> placeOrder()

        );
    }

    private void calculateShippingFee() {
        checkoutViewModel.createOrder(tvAddress.getText().toString(), selectedProducts.get(0).getStoreId());
        checkoutViewModel.shippingFee.observe(this, shippingFee -> {
            tvTotalShippingPrice.setText(shippingFee + " đ");

            int price = Integer.parseInt(tvTotalProductPrice.getText().toString().replaceAll("[^0-9]", ""));
            int totalPrice = price + Integer.parseInt(String.valueOf(shippingFee));
            tvTotalPrice.setText(totalPrice + " đ");
            checkoutViewModel.updateStatusOrder(orderId, shippingFee);
        });
    }

    private void placeOrder() {
        String address = tvAddress.getText().toString();
        String storeId = selectedProducts.get(0).getStoreId();
        String username = tvUserName.getText().toString();
        String phonenumber = tvPhoneNumber.getText().toString();

        if (radVNPay.isChecked()) {
            handleVNPayPayment(address, storeId, username, phonenumber);
        } else if (radCOD.isChecked()) {
            handleCODPayment(address, storeId, username, phonenumber);
        } else {
            showPaymentMethodDialog();
        }
    }

    private void handleVNPayPayment(String address, String storeId, String username, String phonenumber) {
        int price = Integer.parseInt(tvTotalPrice.getText().toString().replaceAll("[^0-9]", ""));
        String orderInfo = "Thanh toán đơn hàng " + orderId;
        List<String> productIds = selectedProducts.stream()
                .map(Product::getProduct_id)
                .collect(Collectors.toList());
        Intent intent = new Intent(CheckoutActivity.this, VNPaymentActivity.class);
        intent.putExtra("price", price);
        intent.putExtra("orderInfo", orderInfo);
        intent.putStringArrayListExtra("productIds", new ArrayList<>(productIds));
        intent.putExtra("address", address);
        intent.putExtra("storeId", storeId);
        intent.putExtra("username", username);
        intent.putExtra("phonenumber", phonenumber);
        intent.putParcelableArrayListExtra("products", new ArrayList<>(selectedProducts));

        int shippingFee = Integer.parseInt(tvTotalShippingPrice.getText().toString().replaceAll("[^0-9]", ""));
        checkoutViewModel.updateStatusOrder(orderId, shippingFee);
        startActivity(intent);
    }

    private void handleCODPayment(String address, String storeId, String username, String phonenumber) {
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
                    checkoutViewModel.placeOrder(totalPrice, expectedDeliveryTime, shippingFee, paymentMethod, shippingName, productIds, address, storeId, selectedProducts, username, phonenumber, new CheckoutViewModel.OrderCallback() {
                        @Override
                        public void onSuccess(String orderId) {
                            Log.d("CheckoutActivity", "Order placed successfully: " + orderId);
                            checkoutViewModel.removeOrderedProductsFromCart(FirebaseAuth.getInstance().getCurrentUser().getUid(), new CheckoutViewModel.OrderCallback() {
                                @Override
                                public void onSuccess(String orderId) {
                                    Log.d("CheckoutActivity", "Products removed from cart successfully: " + orderId);
                                    Intent intent = new Intent(CheckoutActivity.this, PlaceOrderActivity.class);
                                    intent.putExtra("orderId", orderId);
                                    checkoutViewModel.loadUserData(tvUserName, tvPhoneNumber, tvAddress);

                                    int shippingFee = Integer.parseInt(tvTotalShippingPrice.getText().toString().replaceAll("[^0-9]", ""));
                                    checkoutViewModel.updateStatusOrder(orderId, shippingFee);

                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("CheckoutActivity", "Failed to remove products from cart", e);
                                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }, orderId);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("CheckoutActivity", "Failed to place order", e);
                            Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                })
                .show();
    }

    private void showPaymentMethodDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Chọn phương thức thanh toán")
                .setMessage("Vui lòng chọn phương thức thanh toán")
                .setPositiveButton("OK", (dialog, which) -> {
                })
                .show();
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
    private void createOrderWithGHN() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<Map<String, Object>> addresses = (List<Map<String, Object>>) documentSnapshot.get("addresses");

                    if (addresses != null && !addresses.isEmpty()) {
                        Map<String, Object> address = addresses.get(0);

                        String province = (String) address.get("province");
                        String district = (String) address.get("district");
                        String commune = (String) address.get("commune");
                        String street = (String) address.get("street");

                        String address5 = street + ", " + commune + ", " + district + ", " + province;
                        checkoutViewModel.createOrder(address5, selectedProducts.get(0).getStoreId());
                        checkoutViewModel.shippingFee.observe(CheckoutActivity.this, shippingFee -> {


                            int price = Integer.parseInt(tvTotalProductPrice.getText().toString().replaceAll("[^0-9]", ""));
                            int totalPrice = price + Integer.parseInt(String.valueOf(shippingFee));


                            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                            tvTotalShippingPrice.setText(format.format(shippingFee));

                            String totalPriceString = format.format(totalPrice);
                            tvFinalTotalPrice.setText(totalPriceString);
                            tvTotalPrice.setText(totalPriceString);

                        });
                    }
                });
    }
}