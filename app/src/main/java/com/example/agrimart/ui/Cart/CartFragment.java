package com.example.agrimart.ui.Cart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.agrimart.R;
import com.example.agrimart.adapter.StoreCartAdapter;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.viewmodel.CartFragmentViewModel;

import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView rvCarts;
    private StoreCartAdapter storeCartAdapter;
    private CartFragmentViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Cập nhật màu status bar
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        // Khởi tạo RecyclerView
        rvCarts = view.findViewById(R.id.rv_cart);
        rvCarts.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // Khởi tạo ViewModel
        viewModel = new CartFragmentViewModel();



        viewModel.getStoreCartsByUserId(new CartFragmentViewModel.OnDataFetchedListener() {
            @Override
            public void onDataFetched(List<Cart> storeCarts) {
                // Cập nhật RecyclerView với danh sách StoreCart
                storeCartAdapter = new StoreCartAdapter(storeCarts);
                rvCarts.setAdapter(storeCartAdapter);

            }

            @Override
            public void onError(String errorMessage) {
                // Xử lý lỗi nếu không lấy được dữ liệu
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        ImageView checkboxAll = view.findViewById(R.id.checkbox_all);

        checkboxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxAll.getTag() == null || checkboxAll.getTag().equals("unchecked")) {
                    checkboxAll.setImageResource(R.drawable.checkbox_checked);
                    checkboxAll.setTag("checked");

                    // Đánh dấu tất cả sản phẩm trong giỏ hàng
                    if (storeCartAdapter != null) {
                        storeCartAdapter.setCheckedAll(true); // Cập nhật trạng thái tất cả sản phẩm
                    }
                } else {
                    checkboxAll.setImageResource(R.drawable.checkbox_empty);
                    checkboxAll.setTag("unchecked");

                    // Bỏ chọn tất cả sản phẩm trong giỏ hàng
                    if (storeCartAdapter != null) {
                        storeCartAdapter.setCheckedAll(false); // Bỏ chọn tất cả sản phẩm
                    }
                }
            }
        });

        return view;
    }
}
