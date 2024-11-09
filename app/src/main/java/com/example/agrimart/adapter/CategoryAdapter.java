package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.databinding.CategoryItemBinding;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private List<Category> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;
    public Category category;
    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(categories.get(position));

        if (holder.getAdapterPosition() == selectedPosition) {
            holder.binding.getRoot().setBackgroundResource(android.R.color.holo_green_light);
        } else {
            holder.binding.getRoot().setBackgroundResource(android.R.color.transparent);
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            category = categories.get(holder.getAdapterPosition());
            int prePos=selectedPosition;
            selectedPosition=holder.getAdapterPosition();
            notifyItemChanged(prePos);
            notifyItemChanged(holder.getAdapterPosition());

            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CategoryItemBinding binding;

        public MyViewHolder(@NonNull CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(Category category) {
            Glide.with(binding.getRoot())
                    .load(category.getImg())
                    .into(binding.imgCate);

            binding.tvName.setText(category.getName());
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}
