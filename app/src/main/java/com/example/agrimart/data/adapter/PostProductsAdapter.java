// PostProductsAdapter.java
package com.example.agrimart.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.PostProduct;

import java.util.List;

public class PostProductsAdapter extends RecyclerView.Adapter<PostProductsAdapter.PostProductsViewHolder> {

    private final List<PostProduct> postProductList;

    public PostProductsAdapter(List<PostProduct> postProductList) {
        this.postProductList = postProductList;
    }

    @NonNull
    @Override
    public PostProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_your_product_listings, parent, false);
        return new PostProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostProductsViewHolder holder, int position) {
        PostProduct postProduct = postProductList.get(position);
        holder.tvIconTitle.setText(postProduct.getTitle());
        holder.tvPrice.setText(postProduct.getPrice());
        holder.imageView.setImageResource(postProduct.getImageResId());  // Cập nhật ảnh
    }

    @Override
    public int getItemCount() {
        return postProductList.size();
    }

    static class PostProductsViewHolder extends RecyclerView.ViewHolder {
        TextView tvIconTitle;
        TextView tvPrice;
        ImageView imageView;  // Thêm ImageView
        AppCompatButton btnDetailPL;

        public PostProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIconTitle = itemView.findViewById(R.id.tvIconTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageView = itemView.findViewById(R.id.imageView);  // Khởi tạo ImageView
            btnDetailPL = itemView.findViewById(R.id.btn_detailPL);
        }
    }
}
