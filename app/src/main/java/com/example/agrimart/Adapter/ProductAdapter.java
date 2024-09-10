package com.example.agrimart.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.Model.Category;
import com.example.agrimart.Model.Product;
import com.example.agrimart.databinding.CategoryItemBinding;
import com.example.agrimart.databinding.ProductItemBinding;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductItemBinding binding=ProductItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ProductItemBinding binding;
        public MyViewHolder(@NonNull ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bindData(Product product){
            binding.imgPro.setImageResource(product.getImage());
            binding.tvName.setText(product.getName());
            binding.tvPrice.setText(product.getPrice());
            binding.tvUnit.setText(product.getUnit());
            binding.tvActive.setText(product.getActive());
        }
    }
}
