package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Product;

import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ProductOrderViewHolder> {

    private List<Product> productList;
    private OnProductClickListener productClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    // Constructor
    public ProductOrderAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
        return new ProductOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderViewHolder holder, int position) {
        Product product = productList.get(position);

        // Gắn dữ liệu vào các view trong CardView
        holder.tvProductName.setText(product.getName());
        holder.tvUnit.setText(product.getUnit());
        holder.tvQuantity.setText("x" + product.getQuantity());
        holder.tvPrice.setText(String.format("%,.0f", product.getPrice()));
        Glide.with(holder.itemView.getContext())
                .load(product.getImages().get(0))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> {
            if(productClickListener != null){
                productClickListener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class để ánh xạ các view
    public static class ProductOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvUnit, tvQuantity, tvPrice;
        ImageView imgProduct;

        public ProductOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvUnit = itemView.findViewById(R.id.unit);
            tvQuantity = itemView.findViewById(R.id.quantity);
            tvPrice = itemView.findViewById(R.id.price);
            imgProduct = itemView.findViewById(R.id.img_product);
        }
    }

    // Cập nhật dữ liệu trong adapter
    public void updateProductList(List<Product> newProductList) {
        productList = newProductList;
        notifyDataSetChanged();
    }
}
