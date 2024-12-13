package com.example.agrimart.ui.MyProfile;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.agrimart.R;
import com.example.agrimart.ui.Account.SignInActivity;
import com.example.agrimart.ui.MyProfile.MyAccount.MyAccountActivity;
import com.example.agrimart.ui.MyProfile.MyAddress.MyAddressActivity;
import com.example.agrimart.ui.MyProfile.MyStore.MyStoreActivity;
import com.example.agrimart.ui.MyProfile.MyStore.RegisterSellerActivity;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.PurchasedOrdersActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfileFragment extends Fragment {

    private LinearLayout purchase_order, confirm, goods, logout,
            delivery, evaluate, my_store, my_address, setting, my_account, header;
    private TextView userNameTextView;
    private ImageView user_image;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private static final int MY_ACCOUNT_REQUEST_CODE = 2;
    private String userRole;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userRole = sharedPreferences.getString("user_role", "buyer");
        addControl(view);
        addEvents();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        loadUserInfo();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
            loadUserInfo(); // Gọi phương thức để tải lại thông tin người dùng
        }
    }

    @SuppressLint("WrongViewCast")
    void addControl(View view) {
        userNameTextView = view.findViewById(R.id.user_name);
        purchase_order = (LinearLayout)view.findViewById(R.id.purchase_order);
        confirm = (LinearLayout)view.findViewById(R.id.waiting_confirm);
        goods = (LinearLayout)view.findViewById(R.id.waiting_goods);
        delivery = (LinearLayout)view.findViewById(R.id.waiting_delivery);
        evaluate = (LinearLayout)view.findViewById(R.id.evaluate);
        my_store = (LinearLayout)view.findViewById(R.id.my_store);
        my_address = (LinearLayout)view.findViewById(R.id.my_address);
        setting = (LinearLayout)view.findViewById(R.id.setting);
        logout = (LinearLayout)view.findViewById(R.id.logout);
        my_account = (LinearLayout) view.findViewById(R.id.my_account);
        header = (LinearLayout) view.findViewById(R.id.header);
        user_image = (ImageView) view.findViewById(R.id.user_image);
    }

    void addEvents() {
        purchase_order.setOnClickListener(v -> navigateToPurchasedOrders());
        confirm.setOnClickListener(v -> navigateToWaitingConfirm());
        goods.setOnClickListener(v -> navigateToWaitingGoods());
        delivery.setOnClickListener(v -> navigateToWaitingDeliverey());
        evaluate.setOnClickListener(v -> navigateToEcaluate());
        my_store.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            if (sharedPreferences.getString("user_role", "seller").equals("seller")) {
                navigateToMyStore();
            } else {
                showDialog();
            }
        });
        my_address.setOnClickListener(v -> navigateToAddress());
        setting.setOnClickListener(v -> navigateToSettings());
        logout.setOnClickListener(v -> handleLogout());
        header.setOnClickListener(v -> myAccount());
        my_account.setOnClickListener(v -> myAccount());
    }

    private void myAccount() {
        Intent intent = new Intent(requireContext(), MyAccountActivity.class);
        startActivityForResult(intent, MY_ACCOUNT_REQUEST_CODE);
    }

    private void loadUserInfo() {
        String userId = auth.getCurrentUser().getUid(); // Lấy UID của người dùng hiện tại
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Lấy dữ liệu từ document
                            String name = document.getString("fullName");
                            String urlImage = document.getString("userImage");

                            // Cập nhật TextViews
                            userNameTextView.setText(name);

                            // Kiểm tra Fragment có được gắn vào không trước khi tải ảnh
                            if (isAdded()) {
                                if (urlImage != null && !urlImage.isEmpty()) {
                                    Glide.with(this)
                                            .load(urlImage)
                                            .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh khi tải lên
                                            .placeholder(R.drawable.user_img) // ảnh mặc định khi đang tải
                                            .error(R.drawable.user_img) // ảnh mặc định nếu URL không tồn tại hoặc tải ảnh lỗi
                                            .into(user_image);
                                } else {
                                    // Hiển thị ảnh mặc định nếu URL rỗng
                                    Glide.with(this)
                                            .load(R.drawable.user_img)
                                            .apply(RequestOptions.circleCropTransform()) // Bo tròn ảnh mặc định
                                            .into(user_image);
                                }
                            }

                            // Cập nhật TextView my_store_text dựa trên userRole
                            TextView myStoreTextView = getView().findViewById(R.id.my_store_text);
                            if ("seller".equals(userRole)) {
                                myStoreTextView.setText("Cửa hàng của tôi");
                            } else {
                                myStoreTextView.setText("Bắt đầu bán");
                            }
                        }
                    }
                });
    }

    private void navigateToPurchasedOrders() {
        Intent intent = new Intent(requireContext(), PurchasedOrdersActivity.class);
        startActivity(intent);
    }

    private void navigateToAddress() {
        Intent intent = new Intent(requireContext(), MyAddressActivity.class);
        startActivity(intent);
    }

    private void navigateToWaitingConfirm() {
        Intent intent = new Intent(requireContext(), PurchasedOrdersActivity.class);
        intent.putExtra("selectedTab", 0);
        startActivity(intent);
    }

    private void navigateToWaitingGoods() {
        Intent intent = new Intent(requireContext(), PurchasedOrdersActivity.class);
        intent.putExtra("selectedTab", 1);
        startActivity(intent);
    }

    private void navigateToWaitingDeliverey() {
        Intent intent = new Intent(requireContext(), PurchasedOrdersActivity.class);
        intent.putExtra("selectedTab", 2);
        startActivity(intent);
    }

    private void navigateToMyStore() {
        Intent intent = new Intent(requireContext(), MyStoreActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        //Intent intent = new Intent(requireContext(), SettingsActivity.class);
        //startActivity(intent);
    }

    private void handleLogout() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(requireContext(), SignInActivity.class);
        startActivity(intent);
        requireActivity().finish();
        Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }

    private void navigateToEcaluate() {

    }

    void showDialog() {
        Dialog dialog = new Dialog(requireContext());

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_seller, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        Button button = view.findViewById(R.id.btnCreateSeller);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegisterSellerActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        Button btnClose = view.findViewById(R.id.btnCancel);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setCancelable(true);
        dialog.show();
    }
}