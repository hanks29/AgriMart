package com.example.agrimart.data.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.data.model.Category;
import com.example.agrimart.databinding.CategoryItem1Binding;
import com.example.agrimart.databinding.CategoryItemBinding;

import java.util.List;

public class CategoryAdapter1 extends RecyclerView.Adapter<CategoryAdapter1.MyViewHolder> {
    private List<Category> categories;


    public CategoryAdapter1(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItem1Binding binding= CategoryItem1Binding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CategoryItem1Binding binding;
        public MyViewHolder(@NonNull  CategoryItem1Binding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bindData(Category category){
            binding.imgCate.setImageResource(category.getImage());
            binding.tvName.setText(category.getName());
        }
    }
}
