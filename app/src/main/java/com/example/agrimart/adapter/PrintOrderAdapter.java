package com.example.agrimart.adapter;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.service.GHNService;
import com.example.agrimart.databinding.ItemPrintOrderBinding;
import com.example.agrimart.databinding.ItemStatusOrderBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PrintOrderAdapter extends RecyclerView.Adapter<PrintOrderAdapter.MyViewHolder>{
    private List<Order> orderList;

    public PrintOrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public PrintOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPrintOrderBinding binding = ItemPrintOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(orderList.get(position));
        holder.binding.btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GHNService ghnService=new GHNService();
                ghnService.printOrderGHN(orderList.get(holder.getAdapterPosition()).getOrderCode(), new GHNService.Callback<JsonNode>() {
                    @Override
                    public void onResponse(JsonNode result) {
                        String token=result.path("data").path("token").asText();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("orders").document(orderList.get(holder.getAdapterPosition()).getOrderId())
                                .update("status", "PendingPickup")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(holder.binding.getRoot().getContext(), "Vui lòng đóng gói, dán nhãn và chờ shipper đến nhận hàng.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(holder.binding.getRoot().getContext(), "Xin hãy thử lại", Toast.LENGTH_SHORT).show();
                                });
                        String url="https://online-gateway.ghn.vn/a5/public-api/printA5?token="+token;
                        Log.d("PrintOrderAdapter111", "Generated URL: " + url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        holder.binding.getRoot().getContext().startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("PrintOrderAdapter111", "Generated URL: " + e.getMessage());

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
        ItemPrintOrderBinding binding;

        public MyViewHolder(@NonNull ItemPrintOrderBinding binding) {
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
