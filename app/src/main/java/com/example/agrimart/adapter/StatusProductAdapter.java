package com.example.agrimart.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.agrimart.ui.MyProfile.state_order.fragment.AwaitingPickupFragment;
import com.example.agrimart.ui.PostProduct.PendingListFragment;
import com.example.agrimart.ui.PostProduct.YourListProductCancelFragment;
import com.example.agrimart.ui.PostProduct.YourProductListingsFragment;

public class StatusProductAdapter extends FragmentStateAdapter {

    public StatusProductAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:{
                return new YourProductListingsFragment();
            }
            case 1: {
                return new PendingListFragment();
            }
            case 2: {
                return new YourListProductCancelFragment();
            }
            default:
                return new YourProductListingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
