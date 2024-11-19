package com.example.agrimart.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.User;
import com.example.agrimart.data.model.ghn.GHNRequest;
import com.example.agrimart.data.model.ghn.Item;
import com.example.agrimart.data.service.GHNService;
import com.example.agrimart.databinding.ItemStatusOrderBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
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
                String address = orderList.get(holder.getAdapterPosition()).getAddress();
                String[] addressUser=address.split(",");
                String province = addressUser[3].trim();
                String district = addressUser[2].trim();
                String ward = addressUser[1].trim();
                String street = addressUser[0].trim();
                GHNService ghnService = new GHNService();

                db.collection("users").document(orderList.get(holder.getAdapterPosition()).getSellerId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User shop = documentSnapshot.toObject(User.class);
                                ghnService.getProvinceId(province, new GHNService.Callback<Integer>() {
                                    @Override
                                    public void onResponse(Integer result) {
                                        ghnService.getDistrictId(district, result, new GHNService.Callback<Integer>() {
                                            @Override
                                            public void onResponse(Integer result) {
                                                final int districtId = result;
                                                ghnService.getWardCode(ward, result, new GHNService.Callback<String>() {
                                                    @Override
                                                    public void onResponse(String result) {
                                                        db.collection("users").document(orderList.get(holder.getAdapterPosition()).getUserId())
                                                                .get()
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        User user = documentSnapshot.toObject(User.class);
                                                                        GHNRequest request = new GHNRequest();
                                                                        request.setFromName(shop.getFullName());
                                                                        request.setFromPhone(shop.getPhoneNumber());
                                                                        request.setFromAddress(shop.getStoreAddress().getStreet());
                                                                        request.setFromWardName(shop.getStoreAddress().getWard());
                                                                        request.setFromDistrictName(shop.getStoreAddress().getDistrict());
                                                                        request.setFromProvinceName(shop.getStoreAddress().getCity());
                                                                        request.setToName(user.getFullName());
                                                                        request.setToPhone(user.getAddresses().get(0).getPhone());
                                                                        request.setToAddress(street);
                                                                        request.setToWardCode(result);
                                                                        request.setToDistrictId(districtId);
                                                                        request.setServiceTypeId(2);
                                                                        request.setWeight(1000);
                                                                        request.setLength(10);
                                                                        request.setWidth(10);
                                                                        request.setHeight(10);
                                                                        request.setPaymentTypeId(1);
                                                                        request.setRequiredNote("KHONGCHOXEMHANG");
                                                                        request.setCodAmount(orderList.get(holder.getAdapterPosition()).getTotalPrice());
                                                                        Item item = new Item();
                                                                        item.setWeight(100);
                                                                        item.setQuantity(1);
                                                                        item.setName("Product Name");
                                                                        request.setItems(List.of(item));
                                                                        ghnService.createShippingOrder(request, new GHNService.Callback<JsonNode>() {
                                                                            @Override
                                                                            public void onResponse(JsonNode result) {
                                                                                String orderCodes = extractOrderCode(result);
                                                                                db.collection("orders").document(orderList.get(holder.getAdapterPosition()).getOrderId())
                                                                                        .update(
                                                                                                "status", "Approved",
                                                                                                "order_code", orderCodes)
                                                                                        .addOnSuccessListener(aVoid -> {
                                                                                            orderList.remove(holder.getAdapterPosition());
                                                                                            notifyDataSetChanged();

                                                                                        })
                                                                                        .addOnFailureListener(e -> {
                                                                                            Toast.makeText(holder.binding.getRoot().getContext(), "Xác nhận đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                                                                                        });
                                                                                Toast.makeText(holder.binding.getRoot().getContext(), "Đã xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Exception e) {
                                                                                Log.d("ADDRESS_USER", "Order created: " + e.getMessage());
                                                                            }
                                                                        });
                                                                    }
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Log.d("ADDRESS_USER", "Order created: " + e.getMessage());
                                                                });

                                                    }

                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        Log.d("ADDRESS_USER", "Ward code: " + e.getMessage());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                Log.d("ADDRESS_USER", "District code: " + e.getMessage());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("ADDRESS_USER", "Province code: " + e.getMessage());
                                    }
                                });
                            }
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

    private String extractOrderCode(JsonNode responseBody) {
        try {
            return responseBody.path("data").path("order_code").asText();
        }catch (Exception e){
            throw new RuntimeException("Error while extracting order code");
        }
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
