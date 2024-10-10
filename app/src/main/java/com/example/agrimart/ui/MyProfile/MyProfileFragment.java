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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private LinearLayout purchase_order, confirm, goods, logout,
            delivery, evaluate, my_store, my_address, setting;

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

        return view;
    }

    @SuppressLint("WrongViewCast")
    void addControl(View view) {
        purchase_order = (LinearLayout)view.findViewById(R.id.purchase_order);
        confirm = (LinearLayout)view.findViewById(R.id.waiting_confirm);
        goods = (LinearLayout)view.findViewById(R.id.waiting_goods);
        delivery = (LinearLayout)view.findViewById(R.id.waiting_delivery);
        evaluate = (LinearLayout)view.findViewById(R.id.evaluate);
        my_store = (LinearLayout)view.findViewById(R.id.my_store);
        my_address = (LinearLayout)view.findViewById(R.id.my_address);
        setting = (LinearLayout)view.findViewById(R.id.setting);
        logout = (LinearLayout)view.findViewById(R.id.logout);
    }

    void addEvents() {
        // Thêm sự kiện OnClick cho các view
        purchase_order.setOnClickListener(v -> navigateToPurchasedOrders());

        confirm.setOnClickListener(v -> navigateToWaitingConfirm());

        goods.setOnClickListener(v -> navigateToWaitingGoods());

        delivery.setOnClickListener(v -> navigateToWaitingDeliverey());

        evaluate.setOnClickListener(v -> navigateToEcaluate());

        my_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                if (sharedPreferences.getString("user_role", "seller").equals("seller")) {
                    navigateToMyStore();
                } else {
                    showDialog();
                }
            }
        });

        my_address.setOnClickListener(v -> navigateToAddress());

        setting.setOnClickListener(v -> navigateToSettings());

        logout.setOnClickListener(v -> handleLogout());
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

    }

    void showDialog() {
        Toast.makeText(requireContext(), "Bạn chưa đăng kí tài khoản người bán! ", Toast.LENGTH_SHORT).show();
        Dialog dialog = new Dialog(requireContext());

        View view=LayoutInflater.from(requireContext()).inflate(R.layout.dialog_seller,null);
        dialog.setContentView(view);
        Button button=view.findViewById(R.id.btnCreateSeller);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegisterSellerActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });
        Button btnClose=view.findViewById(R.id.btnCancel);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(true);
        dialog.show();
    }


}