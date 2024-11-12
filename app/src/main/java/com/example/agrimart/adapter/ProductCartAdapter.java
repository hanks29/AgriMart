package com.example.agrimart.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.viewmodel.CartFragmentViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductCartAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.quantity.setText(String.valueOf(product.getQuantity()));
        holder.tvPrice.setText(String.format("%,.0f đ", product.getPrice()));
        CartFragmentViewModel viewModel = new CartFragmentViewModel();

        Glide.with(holder.itemView.getContext())
                .load(product.getImages().get(0))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.img_product);

        // Xử lý khi bấm nút giảm số lượng
        holder.btn_decrease.setOnClickListener(v -> {
            if (product.getQuantity() > 1) { // Đảm bảo số lượng không giảm dưới 1
                product.setQuantity(product.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(product.getQuantity())); // Cập nhật hiển thị số lượng
                viewModel.updateProductQuantityInFirebase(product.getProduct_id(), product.getQuantity());
            }
        });

        // Xử lý khi bấm nút tăng số lượng
        holder.btn_increase.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(product.getQuantity())); // Cập nhật hiển thị số lượng
            viewModel.updateProductQuantityInFirebase(product.getProduct_id(), product.getQuantity());
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, quantity;
        ShapeableImageView img_product;
        ImageView btn_decrease, btn_increase;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.price);
            img_product = itemView.findViewById(R.id.img_product);
            quantity = itemView.findViewById(R.id.cart_quantity_product);
            btn_decrease = itemView.findViewById(R.id.btn_decrease);
            btn_increase = itemView.findViewById(R.id.btn_increase);
        }
    }



}
