package com.example.agrimart.ui.MyProfile.PurchasedOrders.RequestReturn;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ImageProductAdapter;
import com.example.agrimart.adapter.ProductOrderAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.ui.Payment.VnpayRefund;
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RequestReturnActivity extends AppCompatActivity {
    RecyclerView recyclerViewDetail, rvPhotos;
    EditText edtDescribe;
    Order order;
    TextView totalPrice, reasonText;
    ImageButton btnBack, imageCamera;
    AppCompatButton btnGui;
    private OrderStatusFragmentViewModel viewModel;
    LinearLayout myReason, imageButtonCamera;
    private List<Uri> imageUris = new ArrayList<>();
    private ImageProductAdapter imageProductAdapter;
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

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


    void addControl() {
        recyclerViewDetail = findViewById(R.id.recyclerViewDetail);
        totalPrice = findViewById(R.id.total_price);
        btnBack = findViewById(R.id.btn_back);
        btnGui = findViewById(R.id.btn_gui);
        myReason = findViewById(R.id.my_reason);
        reasonText = findViewById(R.id.reason_text);
        imageButtonCamera = findViewById(R.id.ll_imageButtonCamera);
        rvPhotos = findViewById(R.id.rvPhotos);
        imageCamera = findViewById(R.id.imageButtonCamera);
        edtDescribe = findViewById(R.id.edtDescribe);

        viewModel = new OrderStatusFragmentViewModel();
        imageProductAdapter = new ImageProductAdapter(imageUris);
        rvPhotos.setAdapter(imageProductAdapter);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @SuppressLint("SetTextI18n")
    void loadDetail() {
        List<Product> products = order.getProducts();
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products);
        recyclerViewDetail.setAdapter(productOrderAdapter);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));

        totalPrice.setText(formatCurrency(order.getTotalPrice()) + " đ");

        pickMultipleMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(2), uris -> {
                    if (!uris.isEmpty()) {
                        imageUris.addAll(uris);
                        rvPhotos.setVisibility(View.VISIBLE);
                        imageProductAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    private void addEvent() {
        btnBack.setOnClickListener(v -> finish());
        btnGui.setOnClickListener(v -> {
            requestReturn();
        });
        myReason.setOnClickListener(v -> showReasonDialog((String) reasonText.getText()));


        imageCamera.setOnClickListener(v -> {
            openImageCamera();
        });
        imageButtonCamera.setOnClickListener(v -> {
            openImageCamera();
        });
    }

    void openImageCamera() {
        if (imageUris.size() < 4 || Objects.isNull(imageProductAdapter.imageUris) || imageProductAdapter.imageUris.isEmpty()) {
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else {
            Toast.makeText(this, "Vui long xóa ảnh đã chọn để thêm ảnh mới", Toast.LENGTH_SHORT).show();
        }
    }

    private void showReasonDialog(String s) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_reason);

        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.reason_radio_group);
        AppCompatButton btnSubmit = bottomSheetDialog.findViewById(R.id.btn_submit_reason);

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

        if (imageUris.isEmpty()) {
            Toast.makeText(this, "Không có ảnh nào để tải lên.", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = (String) reasonText.getText();
        String describe = String.valueOf(edtDescribe.getText());

        if (reason.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập lý do trả hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (describe.isEmpty()) {
            Toast.makeText(this, "Vui lòng mô tả chi tiết lý do trả hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.updateOrderStatusReturn(order.getOrderId(), "return",  reason, describe,imageUris, new OrderStatusFragmentViewModel.OnStatusUpdateListener() {
            @Override
            public void onSuccess(String message) {
                order.setStatus(order.getStatus());
            }

            @Override
            public void onError(String errorMessage) {
                new android.os.Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(RequestReturnActivity.this, "Không thể hủy đơn hàng: ", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private String formatCurrency(double amount) {
        return NumberFormat.getInstance(Locale.getDefault()).format(amount);
    }


}