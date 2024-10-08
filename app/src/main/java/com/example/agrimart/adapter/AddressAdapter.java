package com.example.agrimart.adapter;

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

import java.util.Collections;
import java.util.Comparator;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> addressList;
    private float density;

    public AddressAdapter(Context context, List<Address> addressList) {
        // Sắp xếp danh sách địa chỉ theo isDefault = true lên đầu
        Collections.sort(addressList, new Comparator<Address>() {
            @Override
            public int compare(Address a1, Address a2) {
                return Boolean.compare(a2.isDefault(), a1.isDefault()); // isDefault = true lên trước
            }
        });

        this.addressList = addressList;
        this.density = context.getResources().getDisplayMetrics().density;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.tvNameAddress.setText(address.getName());
        holder.tvPhoneAddress.setText(address.getPhone());
        holder.tvStreetAddress.setText(address.getStreet());
        holder.tvAddress.setText(address.getDetailedAddress());

        // Hiển thị hoặc ẩn thông tin địa chỉ mặc định
        holder.tvDefaultAddress.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        // Thiết lập margin cho divider
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.divider.getLayoutParams();
        if (position == addressList.size() - 1) {
            params.setMargins(0, (int) (20 * density), 0, 0); // Không có margin cho item cuối
        } else {
            params.setMargins((int) (20 * density), (int) (20 * density), 0, 0); // Margin cho các item khác
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
