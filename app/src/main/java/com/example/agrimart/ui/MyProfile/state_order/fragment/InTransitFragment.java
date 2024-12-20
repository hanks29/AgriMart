package com.example.agrimart.ui.MyProfile.state_order.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ShippingAdapter;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.databinding.FragmentInTransitBinding;
import com.example.agrimart.viewmodel.PendingConfirmOrderViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InTransitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InTransitFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PendingConfirmOrderViewModel viewModel;
    private FragmentInTransitBinding binding;

    private List<Order> orderList;
    private ShippingAdapter shippingAdapter;

    public InTransitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InTransitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InTransitFragment newInstance(String param1, String param2) {
        InTransitFragment fragment = new InTransitFragment();
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
        binding = FragmentInTransitBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(PendingConfirmOrderViewModel.class);
        viewModel.getOrderWithStatusDelivering();

        orderList= new ArrayList<>();
        shippingAdapter = new ShippingAdapter(orderList);
        binding.rvOrder.setAdapter(shippingAdapter);
        binding.rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));


        viewModel.orderListPicked.observe(getViewLifecycleOwner(), orders -> {
            orderList.clear();
            orderList.addAll(orders);
            Log.d("PrintOrderAdapter111", "onSuccess12: "+orders.size()+" "+orderList.size());
            shippingAdapter.notifyDataSetChanged();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDeliveringOrders();
    }

    public void getDeliveringOrders(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .whereEqualTo("status", "delivering")
                .whereEqualTo("storeId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                        orderList.clear();
                        orderList.addAll(orders);
                        shippingAdapter.notifyDataSetChanged();
                        Log.d("PrintOrderAdapter111", "onSuccess: "+orders.size());
                    }
                })
                .addOnFailureListener(e -> {
                    orderList.clear();
                });
    }
}