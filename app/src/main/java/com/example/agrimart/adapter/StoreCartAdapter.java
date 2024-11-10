package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.StoreCart;

import java.util.List;

public class StoreCartAdapter extends RecyclerView.Adapter<StoreCartAdapter.StoreCartViewHolder> {

    private List<StoreCart> storeCartList;

    public StoreCartAdapter(List<StoreCart> storeCartList) {
        this.storeCartList = storeCartList;
    }

    @NonNull
    @Override
    public StoreCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_store, parent, false);
        return new StoreCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCartViewHolder holder, int position) {
        StoreCart storeCart = storeCartList.get(position);
        holder.tvStoreName.setText(storeCart.getStoreId());

        // Cài đặt RecyclerView con cho danh sách sản phẩm
        ProductCartAdapter productAdapter = new ProductCartAdapter((storeCart.getProducts()));
        holder.rvProducts.setAdapter(productAdapter);
        holder.rvProducts.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return storeCartList.size();
    }

    static class StoreCartViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoreName;
        RecyclerView rvProducts;

        public StoreCartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            rvProducts = itemView.findViewById(R.id.rv_products);
        }
    }
}
