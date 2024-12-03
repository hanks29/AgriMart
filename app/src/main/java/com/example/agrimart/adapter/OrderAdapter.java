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
import com.example.agrimart.data.model.User;
import com.example.agrimart.data.model.ghn.GHNRequest;
import com.example.agrimart.data.model.ghn.Item;
import com.example.agrimart.data.service.GHNService;
import com.example.agrimart.databinding.ItemStatusOrderBinding;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
                                                                        request.setFromAddress(shop.getStoreAddress().getStreet()+", "+shop.getStoreAddress().getWard()+", "+shop.getStoreAddress().getDistrict()+", "+shop.getStoreAddress().getCity());
                                                                        request.setFromWardName(shop.getStoreAddress().getWard());
                                                                        request.setFromDistrictName(shop.getStoreAddress().getDistrict());
                                                                        request.setFromProvinceName(shop.getStoreAddress().getCity());
                                                                        request.setToName(user.getFullName());
                                                                        request.setToPhone(user.getAddresses().get(0).getPhone());
                                                                        request.setToAddress(street+", "+ward+", "+district+", "+province);
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
                                                                        List<Item> item=new ArrayList<>();
                                                                        for (int i=0;i<orderList.get(holder.getAdapterPosition()).getProducts().size();i++){
                                                                            Item it = new Item();
                                                                            it.setWeight(100);
                                                                            it.setQuantity(orderList.get(holder.getAdapterPosition()).getProducts().get(i).getQuantity());
                                                                            it.setName(orderList.get(holder.getAdapterPosition()).getProducts().get(i).getName());
                                                                            item.add(it);
                                                                        }
                                                                        request.setItems(item);
                                                                        ghnService.createShippingOrder(request, new GHNService.Callback<JsonNode>() {
                                                                            @Override
                                                                            public void onResponse(JsonNode result) {
                                                                                String orderCodes = extractOrderCode(result);
                                                                                db.collection("orders").document(orderList.get(holder.getAdapterPosition()).getOrderId())
                                                                                        .update(
                                                                                                "status", "approved",
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
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("orders").document(orderList.get(holder.getAdapterPosition()).getOrderId())
                        .update("status", "canceled")
                        .addOnSuccessListener(aVoid -> {
                            for(int i=0;i<orderList.get(holder.getAdapterPosition()).getProducts().size();i++){
                                int index=i;
                                db.collection("products").document(orderList.get(holder.getAdapterPosition()).getProducts().get(i).getProduct_id())
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            Product product = documentSnapshot.toObject(Product.class);
                                            db.collection("products").document(product.getProduct_id())
                                                    .update("quantity", product.getQuantity() + orderList.get(holder.getAdapterPosition()).getProducts().get(index).getQuantity())
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        orderList.remove(holder.getAdapterPosition());
                                                        notifyDataSetChanged();
                                                        Toast.makeText(holder.binding.getRoot().getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(holder.binding.getRoot().getContext(), "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(holder.binding.getRoot().getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                                        });
                            }

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.binding.getRoot().getContext(), "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
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
                    int index=i;
                    db.collection("products").document(orderList.get(holder.getAdapterPosition()).getProducts().get(i).getProduct_id())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Product product = documentSnapshot.toObject(Product.class);
                                    product.setQuantity(orderList.get(holder.getAdapterPosition()).getProducts().get(index).getQuantity());
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
