package com.example.agrimart.ui.MyProfile.state_order.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.R;
import com.example.agrimart.adapter.OrderAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.databinding.FragmentPendingConfirmationBinding;
import com.example.agrimart.viewmodel.PendingConfirmOrderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingConfirmationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PendingConfirmOrderViewHolder viewHolder;
    private FragmentPendingConfirmationBinding binding;

    private List<Order> orderList;
    private OrderAdapter adapter;
    public PendingConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingConfirmationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingConfirmationFragment newInstance(String param1, String param2) {
        PendingConfirmationFragment fragment = new PendingConfirmationFragment();
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

        viewHolder = new ViewModelProvider(this).get(PendingConfirmOrderViewHolder.class);
        binding = FragmentPendingConfirmationBinding.inflate(inflater, container, false);

        orderList = new ArrayList<>();
        viewHolder.getOrderListFromFirebase();
        adapter=new OrderAdapter(orderList);
        binding.rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOrder.setAdapter(adapter);

        viewHolder.orderList.observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                orderList.clear();
                orderList.addAll(orders);
                adapter.notifyDataSetChanged();
            }
        });
        return binding.getRoot();
    }
}