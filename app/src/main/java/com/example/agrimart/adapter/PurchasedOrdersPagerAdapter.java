package com.example.agrimart.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.agrimart.ui.MyProfile.PurchasedOrders.CancelledFragment;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.WaitingConfirmFragment;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.DeliveredFragment;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.WaitingDelivereyFragment;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.ReturnFragment;
import com.example.agrimart.ui.MyProfile.PurchasedOrders.WaitingGoodsFragment;

public class PurchasedOrdersPagerAdapter extends FragmentStateAdapter {

    public PurchasedOrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WaitingConfirmFragment();
            case 1:
                return new WaitingGoodsFragment();
            case 2:
                return new WaitingDelivereyFragment();
            case 3:
                return new DeliveredFragment();
            case 4:
                return new CancelledFragment();
            case 5:
                return new ReturnFragment();

            // Thêm các case cho các tab khác nếu cần
            default:
                return new WaitingConfirmFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 6; // Số lượng tab
    }
}
