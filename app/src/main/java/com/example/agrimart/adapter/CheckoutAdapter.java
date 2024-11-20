package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ProductViewHolder> {
    private List<Product> productList;

    public CheckoutAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        //holder.price.setText(formatPrice(product.getPrice()));
        holder.quantityProduct.setText("x" + product.getQuantity());
        Picasso.get().load(product.getImages().get(0)).into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(price);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView price;
        TextView quantityProduct;
        ImageView imgProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            price = itemView.findViewById(R.id.price);
            quantityProduct = itemView.findViewById(R.id.quantity_product);
            imgProduct = itemView.findViewById(R.id.img_product);
        }
    }
}