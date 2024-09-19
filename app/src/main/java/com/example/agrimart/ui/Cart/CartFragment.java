package com.example.agrimart.ui.Cart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.agrimart.R;
import com.example.agrimart.databinding.FragmentCartBinding;
import com.example.agrimart.databinding.FragmentHomeBinding;

public class CartFragment extends Fragment {
    Button btnCheckout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        btnCheckout = view.findViewById(R.id.btn_checkout);

        btnCheckout.setOnClickListener(View -> {
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            startActivity(intent);
        });

        return view;
    }
}