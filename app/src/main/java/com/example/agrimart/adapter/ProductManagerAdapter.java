package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.ItemProductBinding;
import com.example.agrimart.databinding.ItemProductManagementBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductManagerAdapter extends RecyclerView.Adapter<ProductManagerAdapter.MyViewHolder>{
    private List<Product> products;
    private ProductManagerAdapter.OnItemClickListener listener;
    private boolean isEditMode = false;

    public List<Product> selectedProducts = new ArrayList<>();
    public ProductManagerAdapter(List<Product> products) {
        this.products = products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }
    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    @NonNull
    @Override
    public ProductManagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductManagementBinding binding = ItemProductManagementBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductManagerAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductManagerAdapter.MyViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bindData(product);
        holder.binding.cbDelete.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        holder.binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedProducts.add(product);
            } else {
                selectedProducts.remove(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductManagementBinding binding;

        public MyViewHolder(@NonNull ItemProductManagementBinding binding) {
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
