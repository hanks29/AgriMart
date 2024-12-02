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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.service.GHNService;
import com.example.agrimart.databinding.ItemPrintOrderBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

                        String url="https://dev-online-gateway.ghn.vn/a5/public-api/printA5?token="+token;
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
        holder.binding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("orders").document(orderList.get(holder.getAdapterPosition()).getOrderId())
                        .update("status", "delivering")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(holder.binding.getRoot().getContext(), "Cập nhật trạng thái thành công.", Toast.LENGTH_SHORT).show();
                            orderList.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.binding.getRoot().getContext(), "Xin hãy thử lại", Toast.LENGTH_SHORT).show();
                        });
            }
        });
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
        ItemPrintOrderBinding binding;

        public MyViewHolder(@NonNull ItemPrintOrderBinding binding) {
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

        }
    }
}
