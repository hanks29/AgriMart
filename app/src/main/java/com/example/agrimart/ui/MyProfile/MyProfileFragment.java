package com.example.agrimart.ui.MyProfile;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.agrimart.R;
import com.example.agrimart.ui.Account.SignInActivity;
import com.example.agrimart.ui.MyProfile.MyAddress.MyAddressActivity;
import com.example.agrimart.ui.MyProfile.MyStore.MyStoreActivity;
import com.example.agrimart.ui.MyProfile.MyStore.RegisterSellerActivity;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.PurchasedOrdersActivity;
import android.content.Context;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfileFragment extends Fragment {

    private LinearLayout purchase_order, confirm, goods, logout,
            delivery, evaluate, my_store, my_address, setting;
    private TextView userNameTextView;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        addControl(view);
        addEvents();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        loadUserFullName();

        return view;
    }

    @SuppressLint("WrongViewCast")
    void addControl(View view) {
        purchase_order = view.findViewById(R.id.purchase_order);
        confirm = view.findViewById(R.id.waiting_confirm);
        goods = view.findViewById(R.id.waiting_goods);
        delivery = view.findViewById(R.id.waiting_delivery);
        evaluate = view.findViewById(R.id.evaluate);
        my_store = view.findViewById(R.id.my_store);
        my_address = view.findViewById(R.id.my_address);
        setting = view.findViewById(R.id.setting);
        logout = view.findViewById(R.id.logout);
        userNameTextView = view.findViewById(R.id.user_name); // Ensure this ID matches the layout
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
    }

    private void loadUserFullName() {
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        if (fullName != null) {
                            userNameTextView.setText(fullName);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
        }
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
        intent.putExtra("selectedTab", 0);  // Tab thứ 2 có chỉ số là 1
        startActivity(intent);
    }

    private void navigateToWaitingGoods() {
        Intent intent = new Intent(requireContext(), PurchasedOrdersActivity.class);
        intent.putExtra("selectedTab", 1);  // Tab thứ 2 có chỉ số là 1
        startActivity(intent);
    }

    private void navigateToWaitingDeliverey() {
        Intent intent = new Intent(requireContext(), PurchasedOrdersActivity.class);
        intent.putExtra("selectedTab", 2);  // Tab thứ 2 có chỉ số là 1
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
        // Implement navigation to evaluate
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