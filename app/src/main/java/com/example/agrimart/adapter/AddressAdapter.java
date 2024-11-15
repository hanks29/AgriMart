package com.example.agrimart.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Address;
import java.util.List;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> addressList;
    private final float density;
    private final OnAddressClickListener listener;

    public AddressAdapter(Context context, List<Address> addressList, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.density = context.getResources().getDisplayMetrics().density;
        this.listener = listener;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    // Cập nhật danh sách địa chỉ và thông báo cho RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    public void updateAddresses(List<Address> addresses) {
        this.addressList = addresses;
        notifyDataSetChanged();
    }

    // Interface for address click listener
    public interface OnAddressClickListener {
        void onAddressClick(Address address);
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_address, parent, false);
        return new AddressViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        holder.tvNameAddress.setText(address.getName());
        holder.tvPhoneAddress.setText(address.getPhone());
        holder.tvStreetAddress.setText(address.getStreet());
        holder.tvAddress.setText(address.getCommune() + ", " + address.getDistrict() + ", " + address.getProvince());

        holder.tvDefaultAddress.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressClick(address);
            }
        });

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.divider.getLayoutParams();
        if (position == addressList.size() - 1) {
            params.setMargins(0, (int) (20 * density), 0, 0);
        } else {
            params.setMargins((int) (20 * density), (int) (20 * density), 0, 0);
        }
        holder.divider.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameAddress;
        TextView tvPhoneAddress;
        TextView tvStreetAddress;
        TextView tvAddress;
        TextView tvDefaultAddress;
        View divider;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameAddress = itemView.findViewById(R.id.tvNameAddress);
            tvPhoneAddress = itemView.findViewById(R.id.tvPhoneAddress);
            tvStreetAddress = itemView.findViewById(R.id.tvStreetAddress);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDefaultAddress = itemView.findViewById(R.id.tvDefaultAddress);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
