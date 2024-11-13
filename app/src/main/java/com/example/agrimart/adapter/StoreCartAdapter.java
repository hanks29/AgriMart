package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.viewmodel.CartFragmentViewModel;

import java.util.List;

public class StoreCartAdapter extends RecyclerView.Adapter<StoreCartAdapter.StoreCartViewHolder> {

    private List<Cart> storeCartList;
    CartFragmentViewModel viewModel;

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
        viewModel = new CartFragmentViewModel();

        List<Product> products = storeCart.getProducts();
        ProductCartAdapter productAdapter = new ProductCartAdapter(products, viewModel);

        productAdapter.setOnAllProductsCheckedListener(allChecked -> {
            storeCart.setChecked(allChecked);

            // Cập nhật trạng thái checkbox của cửa hàng khi tất cả sản phẩm được chọn
            if (allChecked) {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_checked);
                holder.checkbox_store.setTag("checked");
            } else {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_empty);
                holder.checkbox_store.setTag("unchecked");
            }

            // Cập nhật lại trạng thái trong Firebase nếu cần
            viewModel.updateStoreCheckedStatusInFirebase(storeCart.getStoreId(), storeCart.getUserId(), allChecked);
        });

        holder.rvProducts.setAdapter(productAdapter);
        holder.rvProducts.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        // Cài đặt checkbox_store ban đầu dựa trên trạng thái của cửa hàng
        if (storeCart.isChecked()) {
            holder.checkbox_store.setImageResource(R.drawable.checkbox_checked);
            holder.checkbox_store.setTag("checked");
        } else {
            holder.checkbox_store.setImageResource(R.drawable.checkbox_empty);
            holder.checkbox_store.setTag("unchecked");
        }

        holder.checkbox_store.setOnClickListener(v -> {
            boolean newCheckedStatus = !(holder.checkbox_store.getTag() != null && holder.checkbox_store.getTag().equals("checked"));
            storeCart.setChecked(newCheckedStatus);

            for (Product product : products) {
                product.setChecked(newCheckedStatus);
            }

            if (newCheckedStatus) {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_checked);
                holder.checkbox_store.setTag("checked");
            } else {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_empty);
                holder.checkbox_store.setTag("unchecked");
            }

            viewModel.updateStoreCheckedStatusInFirebase(storeCart.getStoreId(), storeCart.getUserId(), newCheckedStatus);

            // Cập nhật lại UI của tất cả sản phẩm trong RecyclerView con
            productAdapter.notifyDataSetChanged();
        });
    }


    // StoreCartAdapter.java
    public void setCheckedAll(boolean isChecked) {
        for (Cart storeCart : storeCartList) {
            storeCart.setChecked(isChecked); // Cập nhật trạng thái của storeCart
            for (Product product : storeCart.getProducts()) {
                product.setChecked(isChecked); // Cập nhật trạng thái của từng sản phẩm
            }
            viewModel.updateStoreCheckedStatusInFirebase(storeCart.getStoreId(), storeCart.getUserId(), isChecked);
        }
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }

    @Override
    public int getItemCount() {
        return storeCartList.size();
    }

    static class StoreCartViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoreName;
        RecyclerView rvProducts;
        ImageView checkbox_store;

        public StoreCartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            rvProducts = itemView.findViewById(R.id.rv_products);
            checkbox_store = itemView.findViewById(R.id.checkbox_store);
        }
    }
}
