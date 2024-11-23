package com.example.agrimart.ui.MyProfile.PurchasedOrders;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agrimart.R;
import com.example.agrimart.adapter.OrderStoreAdapter;
import com.example.agrimart.adapter.ProductCartAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.viewmodel.OrderStatusFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusFragment extends Fragment {
    private OrderStatusFragmentViewModel viewModel;
    private String status;
    private TextView text;
    private OrderStoreAdapter orderStoreAdapter;
    RecyclerView recyclerView;

    public OrderStatusFragment(String status) {
        // Required empty public constructor
        this.status = status;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại dữ liệu khi tab được hiển thị
        if (viewModel != null) {
            viewModel.getData(status);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString("status");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_status, container, false);

        addControl(view);

        return view;
    }

    private void addControl(View view) {

        recyclerView = view.findViewById(R.id.recyclerViewOrders);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(OrderStatusFragmentViewModel.class);

        // Khởi tạo Adapter
        orderStoreAdapter = new OrderStoreAdapter(new ArrayList<>(), viewModel);
        recyclerView.setAdapter(orderStoreAdapter);

        viewModel.getData(status);

        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {

            orderStoreAdapter.updateOrders(orders);
        });

    }
}
