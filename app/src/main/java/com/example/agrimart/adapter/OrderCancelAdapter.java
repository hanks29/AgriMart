package com.example.agrimart.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.service.GHNService;
import com.example.agrimart.databinding.ItemOrderBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderCancelAdapter extends RecyclerView.Adapter<OrderCancelAdapter.MyViewHolder>{
    private List<Order> orderList;

    public OrderCancelAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderCancelAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(orderList.get(position));
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1=LayoutInflater.from(holder.binding.getRoot().getContext()).inflate(R.layout.dialog_list_product,null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(holder.binding.getRoot().getContext());
                bottomSheetDialog.setContentView(view1);

                RecyclerView rvProduct = view1.findViewById(R.id.rvListProduct);
                List<Product> productList = new ArrayList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                for(int i=0;i<orderList.get(holder.getAdapterPosition()).getProducts().size();i++){
                    db.collection("products").document(orderList.get(holder.getAdapterPosition()).getProducts().get(i).getProduct_id())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Product product = documentSnapshot.toObject(Product.class);
                                    productList.add(product);
                                    if(productList.size()==orderList.get(holder.getAdapterPosition()).getProducts().size()){
                                        ProductOrderAdapter adapter = new ProductOrderAdapter(productList);
                                        rvProduct.setAdapter(adapter);
                                        rvProduct.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext()));
                                        bottomSheetDialog.show();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(holder.binding.getRoot().getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                            });
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;

        public MyViewHolder(@NonNull ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(Order order) {
            binding.tvQuantity.setText(order.getProducts().size() + " sản phẩm");
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvPrice.setText(format.format(order.getTotalPrice()));

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products").document(order.getProducts().get(0).getProduct_id())
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

            binding.tvStatus.setText("Đã hủy");

        }
    }
}
