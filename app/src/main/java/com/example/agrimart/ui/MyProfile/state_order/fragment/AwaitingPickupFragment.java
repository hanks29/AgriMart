package com.example.agrimart.ui.MyProfile.state_order.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.adapter.PrintOrderAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.databinding.FragmentAwaitingPickupBinding;
import com.example.agrimart.viewmodel.PendingConfirmOrderViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AwaitingPickupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AwaitingPickupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentAwaitingPickupBinding binding;
    private PendingConfirmOrderViewModel viewHolder;

    private List<Order> orderList;
    private PrintOrderAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AwaitingPickupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AwaitingPickupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AwaitingPickupFragment newInstance(String param1, String param2) {
        AwaitingPickupFragment fragment = new AwaitingPickupFragment();
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
        binding = FragmentAwaitingPickupBinding.inflate(inflater, container, false);
        viewHolder = new ViewModelProvider(this).get(PendingConfirmOrderViewModel.class);
        viewHolder.getOrderWithStatusApproved();

        orderList = new ArrayList<>();
        adapter = new PrintOrderAdapter(orderList);
        binding.rvOrder.setAdapter(adapter);
        binding.rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        viewHolder.orderListApproved.observe(getViewLifecycleOwner(), orders -> {
            orderList.clear();
            orderList.addAll(orders);
            adapter.notifyDataSetChanged();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        PendingConfirmOrderViewModel viewHolder=new ViewModelProvider(this).get(PendingConfirmOrderViewModel.class);
        viewHolder.getOrderWithStatusApproved();
        viewHolder.orderListApproved.observe(getViewLifecycleOwner(), orders -> {
            orderList.clear();
            orderList.addAll(orders);
            adapter.notifyDataSetChanged();
        });
    }
}