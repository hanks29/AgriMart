package com.example.agrimart.ui.Cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.agrimart.R;
import com.example.agrimart.adapter.StoreCartAdapter;
import com.example.agrimart.data.model.StoreCart;
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



        // Lấy dữ liệu từ Firestore và cập nhật RecyclerView
        viewModel.getStoreCartsByUserId(new CartFragmentViewModel.OnDataFetchedListener() {
            @Override
            public void onDataFetched(List<StoreCart> storeCarts) {
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

        return view;
    }
}
