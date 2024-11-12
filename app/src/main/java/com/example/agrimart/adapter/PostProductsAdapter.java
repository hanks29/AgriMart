// PostProductsAdapter.java
package com.example.agrimart.adapter;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.ProductResponse;
import com.example.agrimart.databinding.ItemYourProductListingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostProductsAdapter extends RecyclerView.Adapter<PostProductsAdapter.PostProductsViewHolder> {

    private final List<ProductResponse> postProductList;

    private OnItemClickListener onItemClickListener;

    public PostProductsAdapter(List<ProductResponse> postProductList, OnItemClickListener onItemClickListener) {
        this.postProductList = postProductList;
        this.onItemClickListener = onItemClickListener;
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
        holder.binding.btnDetailPL.setOnClickListener(v -> onItemClickListener.onItemClick(postProduct));
        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_product);
                dialog.show();
                Button btnDelete = dialog.findViewById(R.id.btn_delete);
                Button btnClose = dialog.findViewById(R.id.btnCancel);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.deleteProducts(postProduct.getProductId());
                        postProductList.remove(position);
                        notifyItemRemoved(position);
                        holder.deleteProducts(postProduct.getProductId());
                        dialog.dismiss();
                    }
                });
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                return true;
            }
        });
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

        private void deleteProducts(String productId){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference productRef = db.collection("products").document(productId);

            productRef.update("status", "delete")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(itemView.getContext(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(itemView.getContext(), "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    });
        }


    }

    public interface OnItemClickListener {
        void onItemClick(ProductResponse product);
    }
}
