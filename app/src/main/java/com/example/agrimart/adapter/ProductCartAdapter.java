package com.example.agrimart.adapter;

import android.util.Log;
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
import com.example.agrimart.viewmodel.CartFragmentViewModel;
import com.google.android.material.imageview.ShapeableImageView;


import java.util.List;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ProductViewHolder> {

    private List<Product> productList;
    CartFragmentViewModel viewModel;

    public ProductCartAdapter(List<Product> productList, CartFragmentViewModel viewModel) {
        this.productList = productList;
        this.viewModel = viewModel;
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

        Glide.with(holder.itemView.getContext())
                .load(product.getImages().get(0))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.img_product);

        // Thiết lập trạng thái và tag của checkbox sản phẩm
        if (product.isChecked()) {
            holder.checkbox_product.setImageResource(R.drawable.checkbox_checked);
            holder.checkbox_product.setTag("checked");
        } else {
            holder.checkbox_product.setImageResource(R.drawable.checkbox_empty);
            holder.checkbox_product.setTag("unchecked");
        }

        // Xử lý khi bấm nút giảm số lượng
        holder.btn_decrease.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(product.getQuantity()));
                viewModel.updateProductQuantityInFirebase(product.getProduct_id(), product.getStoreId(), product.getQuantity());
            }
        });

        // Xử lý khi bấm nút tăng số lượng
        holder.btn_increase.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(product.getQuantity()));
            viewModel.updateProductQuantityInFirebase(product.getProduct_id(), product.getStoreId(), product.getQuantity());
        });

        // Xử lý khi bấm checkbox sản phẩm
        holder.checkbox_product.setOnClickListener(v -> {
            boolean newCheckedStatus = !(holder.checkbox_product.getTag() != null && holder.checkbox_product.getTag().equals("checked"));
            product.setChecked(newCheckedStatus); // Cập nhật trạng thái checkbox vào Product

            // Cập nhật UI của checkbox sản phẩm
            if (newCheckedStatus) {
                holder.checkbox_product.setImageResource(R.drawable.checkbox_checked);
                holder.checkbox_product.setTag("checked");
                viewModel.updateProductCheckedStatusInFirebase(product.getProduct_id(), product.getStoreId(), true);
            } else {
                holder.checkbox_product.setImageResource(R.drawable.checkbox_empty);
                holder.checkbox_product.setTag("unchecked");
                viewModel.updateProductCheckedStatusInFirebase(product.getProduct_id(), product.getStoreId(), false);
            }


            if (listener != null) {
                listener.onAllProductsChecked(areAllProductsChecked());
            }
        });
    }

    public boolean areAllProductsChecked() {
        for (Product product : productList) {
            if (!product.isChecked()) {
                return false; // Nếu có bất kỳ sản phẩm nào chưa được chọn, trả về false
            }
        }
        return true; // Nếu tất cả sản phẩm đều được chọn, trả về true
    }


    public interface OnAllProductsCheckedListener {
        void onAllProductsChecked(boolean allChecked);
    }

    private OnAllProductsCheckedListener listener;

    public void setOnAllProductsCheckedListener(OnAllProductsCheckedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, quantity;
        ShapeableImageView img_product;
        ImageView btn_decrease, btn_increase, checkbox_product;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.price);
            img_product = itemView.findViewById(R.id.img_product);
            quantity = itemView.findViewById(R.id.cart_quantity_product);
            btn_decrease = itemView.findViewById(R.id.btn_decrease);
            btn_increase = itemView.findViewById(R.id.btn_increase);
            checkbox_product = itemView.findViewById(R.id.checkbox);
        }
    }



}
