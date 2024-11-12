package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Product;

import java.util.List;

public class StoreCartAdapter extends RecyclerView.Adapter<StoreCartAdapter.StoreCartViewHolder> {

    private List<Cart> storeCartList;

    public StoreCartAdapter(List<Cart> storeCartList) {
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
        Cart storeCart = storeCartList.get(position);
        holder.tvStoreName.setText(storeCart.getStore_name());

        // Cài đặt RecyclerView con cho danh sách sản phẩm
        List<Product> products = storeCart.getProducts(); // Giả sử `getProducts()` trả về danh sách sản phẩm
        ProductCartAdapter productAdapter = new ProductCartAdapter(products);
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
