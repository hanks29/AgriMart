package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.ItemProductBinding;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private List<Product> products;
    private OnItemClickListener listener;

    public ProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bindData(product);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        public MyViewHolder(@NonNull ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(Product product) {
            if (product != null) {
                binding.tvName.setText(product.getName() != null ? product.getName() : "N/A");
                Double price = product.getPrice();
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                binding.tvPrice.setText(price != null ? format.format(price) : "0 VNĐ");

                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    Glide.with(binding.getRoot().getContext())
                            .load(product.getImages().get(0))
                            .into(binding.imgPro);
                } else {
                    binding.imgPro.setImageResource(R.drawable.apple);
                }

                binding.executePendingBindings();
            }
        }
    }
}