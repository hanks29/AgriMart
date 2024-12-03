package com.example.agrimart.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.agrimart.data.model.Order;
import com.example.agrimart.data.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderStatusFragmentViewModel extends ViewModel {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private String userId;
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public OrderStatusFragmentViewModel() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void getData(String status) {
        userId = auth.getCurrentUser().getUid();
        firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Order> orderList = task.getResult().toObjects(Order.class);
                        if (!orderList.isEmpty()) {
                            // Lấy danh sách các ID sản phẩm từ đơn hàng
                            fetchProductDetailsAndStore(orderList, new OnDataFetchedListener() {
                                @Override
                                public void onDataFetched(List<Order> updatedOrders) {
                                    orders.setValue(updatedOrders); // Cập nhật LiveData sau khi lấy xong dữ liệu
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    // Handle error
                                    orders.setValue(new ArrayList<>());
                                }
                            });
                        } else {
                            orders.setValue(new ArrayList<>());
                        }
                    } else {
                        orders.setValue(new ArrayList<>());
                    }
                });
    }


    // Cập nhật thông tin của sản phẩm từ collection "products"
    private void fetchProductDetailsAndStore(List<Order> storeOrderList, final OnDataFetchedListener listener) {
        // Khởi tạo danh sách với kích thước bằng storeOrderList để đảm bảo thứ tự
        List<Order> updatedStoreOrderList = new ArrayList<>(storeOrderList.size());
        for (int i = 0; i < storeOrderList.size(); i++) {
            updatedStoreOrderList.add(i, null); // Đảm bảo mỗi vị trí trong danh sách là null trước khi cập nhật
        }

        AtomicInteger remainingRequests = new AtomicInteger(storeOrderList.size());  // Đếm số lượng yêu cầu còn lại

        for (int i = 0; i < storeOrderList.size(); i++) {
            Order storeOrder = storeOrderList.get(i);
            List<Product> products = storeOrder.getProducts();
            AtomicInteger productRequests = new AtomicInteger(products.size());  // Đếm số lượng yêu cầu cho từng sản phẩm

            for (Product product : products) {
                int finalI = i;
                firestore.collection("products")
                        .document(product.getProduct_id())
                        .get()
                        .addOnCompleteListener(productTask -> {
                            if (productTask.isSuccessful()) {
                                DocumentSnapshot productDoc = productTask.getResult();
                                if (productDoc.exists()) {
                                    // Cập nhật thông tin sản phẩm từ Firestore
                                    product.setName(productDoc.getString("name"));
                                    product.setPrice(productDoc.getDouble("price"));
                                    product.setImages((List<String>) productDoc.get("images"));
                                    product.setStoreId(productDoc.getString("storeId"));
                                    product.setUnit(productDoc.getString("unit"));
                                }
                            } else {
                                listener.onError("Lấy thông tin sản phẩm thất bại: " + productTask.getException().getMessage());
                            }

                            // Sau khi tất cả sản phẩm của đơn hàng đã được cập nhật
                            if (productRequests.decrementAndGet() == 0) {
                                firestore.collection("users")
                                        .document(storeOrder.getSellerId())
                                        .get()
                                        .addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful()) {
                                                DocumentSnapshot storeDoc = userTask.getResult();
                                                if (storeDoc.exists()) {
                                                    // Cập nhật thông tin cửa hàng từ Firestore
                                                    storeOrder.setStoreName(storeDoc.getString("store_name"));
                                                }
                                            } else {
                                                listener.onError("Lấy thông tin cửa hàng thất bại: " + userTask.getException().getMessage());
                                            }

                                            // Đặt storeOrder đã được cập nhật vào đúng vị trí trong danh sách
                                            updatedStoreOrderList.set(finalI, storeOrder);

                                            // Sau khi tất cả các yêu cầu đã hoàn thành
                                            if (remainingRequests.decrementAndGet() == 0) {
                                                listener.onDataFetched(updatedStoreOrderList); // Gọi listener khi tất cả dữ liệu đã được lấy về
                                            }
                                        });
                            }
                        });
            }
        }
    }

    public void updateOrderStatus(String orderId, String newStatus, OnStatusUpdateListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("created_at", FieldValue.serverTimestamp()); // Thêm thời gian hiện hành

        firestore.collection("orders")
                .document(orderId)
                .update(updates)
                .addOnSuccessListener(unused -> listener.onSuccess("Order status updated successfully"))
                .addOnFailureListener(e -> listener.onError("Failed to update order status: " + e.getMessage()));
    }


    public void updateOrderStatusRefund(String orderId, String newStatus, OnStatusUpdateListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("created_at", FieldValue.serverTimestamp()); // Thêm thời gian hiện hành
        updates.put("refund", true); // Thêm trường refund với giá trị true

        firestore.collection("orders")
                .document(orderId)
                .update(updates)
                .addOnSuccessListener(unused -> listener.onSuccess("Order status updated successfully"))
                .addOnFailureListener(e -> listener.onError("Failed to update order status: " + e.getMessage()));
    }



    public interface OnStatusUpdateListener {
        void onSuccess(String message);
        void onError(String errorMessage);
    }


    public interface OnDataFetchedListener {
        void onDataFetched(List<Order> updatedOrders);
        void onError(String errorMessage);
    }

}
