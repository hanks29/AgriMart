package com.example.agrimart.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.agrimart.ui.MyProfile.state_order.fragment.AwaitingPickupFragment;
import com.example.agrimart.ui.MyProfile.state_order.fragment.DeliveredFragment;
import com.example.agrimart.ui.MyProfile.state_order.fragment.InTransitFragment;
import com.example.agrimart.ui.MyProfile.state_order.fragment.OrderCancelFragment;
import com.example.agrimart.ui.MyProfile.state_order.fragment.PendingConfirmationFragment;

public class StateOrderAdapter extends FragmentStateAdapter {
    public StateOrderAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:{
                return new PendingConfirmationFragment();
            }
            case 1:{
                return new AwaitingPickupFragment();
            }
            case 2:{
                return new InTransitFragment();
            }
            case 3:{
                return new DeliveredFragment();
            }
            case 4:{
                return new OrderCancelFragment();
            }
            default:
                return new PendingConfirmationFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
