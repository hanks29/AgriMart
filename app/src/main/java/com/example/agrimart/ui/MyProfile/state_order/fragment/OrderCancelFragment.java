package com.example.agrimart.ui.MyProfile.state_order.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.R;
import com.example.agrimart.adapter.OrderCancelAdapter;
import com.example.agrimart.adapter.ShippingAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.databinding.FragmentOrderCancelBinding;
import com.example.agrimart.viewmodel.PendingConfirmOrderViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderCancelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderCancelFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PendingConfirmOrderViewModel viewModel;
    private FragmentOrderCancelBinding binding;

    private List<Order> orderList;
    private OrderCancelAdapter cancelAdapter;

    public OrderCancelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderCancelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderCancelFragment newInstance(String param1, String param2) {
        OrderCancelFragment fragment = new OrderCancelFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderCancelBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(PendingConfirmOrderViewModel.class);
        viewModel.getCancelOrders();

        orderList= new ArrayList<>();
        cancelAdapter = new OrderCancelAdapter(orderList);
        binding.rvOrder.setAdapter(cancelAdapter);
        binding.rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.orderListCancel.observe(getViewLifecycleOwner(), orders -> {
            if(orders != null){
                orderList.clear();
                orderList.addAll(orders);
                cancelAdapter.notifyDataSetChanged();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.orderListCancel.observe(getViewLifecycleOwner(), orders -> {
            if(orders != null){
                orderList.clear();
                orderList.addAll(orders);
                cancelAdapter.notifyDataSetChanged();
            }
        });
    }
}