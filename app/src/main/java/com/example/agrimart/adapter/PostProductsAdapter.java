// PostProductsAdapter.java
package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.PostProduct;
import com.example.agrimart.data.model.ProductResponse;
import com.example.agrimart.databinding.ItemYourProductListingsBinding;

import java.util.List;

public class PostProductsAdapter extends RecyclerView.Adapter<PostProductsAdapter.PostProductsViewHolder> {

    private final List<ProductResponse> postProductList;

    public PostProductsAdapter(List<ProductResponse> postProductList) {
        this.postProductList = postProductList;
    }

    @NonNull
    @Override
    public PostProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemYourProductListingsBinding binding = ItemYourProductListingsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostProductsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostProductsViewHolder holder, int position) {
        ProductResponse postProduct = postProductList.get(position);
        holder.bindData(postProduct);
    }

    @Override
    public int getItemCount() {
        return postProductList.size();
    }

    static class PostProductsViewHolder extends RecyclerView.ViewHolder {
        ItemYourProductListingsBinding binding;

        public PostProductsViewHolder(@NonNull ItemYourProductListingsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindData(ProductResponse postProduct) {
            binding.tvIconTitle.setText(postProduct.getName());
            binding.tvPrice.setText(String.valueOf(postProduct.getPrice()));
            Glide.with(itemView.getContext())
                    .load(postProduct.getImageUrls())
                    .into(binding.imageView);
        }
    }
}
