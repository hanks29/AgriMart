package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.service.GHNService;
import com.example.agrimart.databinding.ItemStatusOrderBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStatusOrderBinding binding = ItemStatusOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(orderList.get(position));
        holder.binding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("orders").document(orderList.get(holder.getAdapterPosition()).getOrderId())
                        .update("status", "Approved")
                        .addOnSuccessListener(aVoid -> {
                            orderList.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();
                            Toast.makeText(holder.binding.getRoot().getContext(), "Đã xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.binding.getRoot().getContext(), "Xác nhận đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        holder.binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GHNService ghnService = new GHNService();
                ghnService.cancelShippingOrder(orderList.get(holder.getAdapterPosition()).getOrderCode(), new GHNService.Callback<JsonNode>() {
                    @Override
                    public void onResponse(JsonNode result) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("orders").document(orderList.get(holder.getAdapterPosition()).getOrderId())
                                .update("status", "Đã hủy")
                                .addOnSuccessListener(aVoid -> {
                                    orderList.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    Toast.makeText(holder.binding.getRoot().getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(holder.binding.getRoot().getContext(), "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(holder.binding.getRoot().getContext(), "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemStatusOrderBinding binding;

        public MyViewHolder(@NonNull ItemStatusOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(Order order) {
            binding.tvQuantity.setText(order.getProducts().size() + " sản phẩm");
            binding.tvPrice.setText(order.getTotalPrice() + " đ");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products").document(order.getProducts().get(0))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Product product = documentSnapshot.toObject(Product.class);
                        Glide.with(binding.getRoot().getContext())
                                .load(product.getImages().get(0))
                                .into(binding.imgPro);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(binding.getRoot().getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    });

        }
    }
}
