package com.example.agrimart.viewmodel;

import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;
import com.example.agrimart.data.model.Address;
import com.example.agrimart.data.model.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CheckoutViewModel extends ViewModel {
    private static final String TAG = "CheckoutViewModel";
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public CheckoutViewModel() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void loadUserData(TextView tvUserName, TextView tvPhoneNumber, TextView tvAddress) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> addresses = (List<Map<String, Object>>) document.get("addresses");
                    if (addresses != null) {
                        List<Address> defaultAddresses = addresses.stream()
                                .map(this::convertToAddress)
                                .filter(Address::isDefault)
                                .collect(Collectors.toList());
                        for (Address address : defaultAddresses) {
                            String name = address.getName();
                            String phone = address.getPhone();
                            Log.d(TAG, "Name: " + name);
                            Log.d(TAG, "Phone: " + phone);
                            tvUserName.setText(name);
                            tvPhoneNumber.setText(phone);
                            tvAddress.setText(address.getStreet() + ", " + address.getCommune() + ", " + address.getDistrict() + ", " + address.getProvince());
                        }
                    }
                }
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private Address convertToAddress(Map<String, Object> map) {
        Address address = new Address();
        address.setAddressId((String) map.get("AddressId"));
        address.setName((String) map.get("name"));
        address.setPhone((String) map.get("phone"));
        address.setStreet((String) map.get("street"));
        address.setCommune((String) map.get("commune"));
        address.setDistrict((String) map.get("district"));
        address.setProvince((String) map.get("province"));
        address.setDetailedAddressID((String) map.get("detailedAddressID"));
        address.setDefault((Boolean) map.get("default"));
        return address;
    }

    public void isUserDataAvailable(UserDataCallback callback) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    List<Map<String, Object>> addresses = (List<Map<String, Object>>) document.get("addresses");
                    if (addresses != null) {
                        List<Address> defaultAddresses = addresses.stream()
                                .map(this::convertToAddress)
                                .filter(Address::isDefault)
                                .collect(Collectors.toList());
                        callback.onResult(!defaultAddresses.isEmpty());
                    } else {
                        callback.onResult(false);
                    }
                } else {
                    callback.onResult(false);
                }
            } else {
                callback.onResult(false);
            }
        });
    }

    public void placeOrder(double totalPrice, String expectedDeliveryTime, double shippingFee, String paymentMethod, String shippingName, List<String> productIds, String address, OrderCallback callback) {
        String userId = auth.getCurrentUser().getUid();
        String orderId = generateOrderId();
        Date createdAt = new Date();

        Map<String, Object> order = new HashMap<>();
        order.put("userId", userId);
        order.put("total_price", totalPrice);
        order.put("expected_delivery_time", expectedDeliveryTime);
        order.put("status", "Pending");
        order.put("shipping_fee", shippingFee);
        order.put("orderId", orderId);
        order.put("paymentMethod", paymentMethod);
        order.put("shipping_name", shippingName);
        order.put("created_at", createdAt);
        order.put("product_id", productIds);
        order.put("address", address);

        db.collection("orders").document(orderId).set(order)
                .addOnSuccessListener(aVoid -> callback.onSuccess(orderId))
                .addOnFailureListener(callback::onFailure);
    }

    private Map<String, Object> convertAddressToMap(Address address) {
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("isDefault", address.isDefault());
        addressMap.put("province", address.getProvince());
        addressMap.put("district", address.getDistrict());
        addressMap.put("commune", address.getCommune());
        addressMap.put("detailedAddressID", address.getDetailedAddressID());
        addressMap.put("name", address.getName());
        addressMap.put("phone", address.getPhone());
        addressMap.put("street", address.getStreet());
        addressMap.put("AddressId", address.getAddressId());
        return addressMap;
    }

    private void updateProductQuantities(List<String> productIds, OrderCallback callback, String orderId) {
        for (String productId : productIds) {
            db.collection("products").document(productId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    long quantity = documentSnapshot.getLong("quantity");
                    db.collection("products").document(productId).update("quantity", quantity - 1)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Product quantity updated.");
                                callback.onSuccess(orderId);
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error updating product quantity", e));
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error getting product", e));
        }
    }

    public void getOrderDetail(String orderId, OrderDetailCallback callback) {
        db.collection("orders").document(orderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> orderDetails = document.getData();
                    callback.onSuccess(orderDetails);
                } else {
                    callback.onFailure(new Exception("Order not found"));
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    public void removeOrderedProductsFromCart(String userId, OrderCallback callback, String orderId) {
        db.collection("cart")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot cartDoc : querySnapshot.getDocuments()) {
                        List<Map<String, Object>> products = (List<Map<String, Object>>) cartDoc.get("products");

                        List<Map<String, Object>> updatedProducts = new ArrayList<>();
                        for (Map<String, Object> product : products) {
                            if (product.get("checked") == null || !(boolean) product.get("checked")) {
                                updatedProducts.add(product);
                            }
                        }

                        Map<String, Object> updatedCart = new HashMap<>();
                        updatedCart.put("products", updatedProducts);

                        db.collection("cart")
                                .document(cartDoc.getId())
                                .update(updatedCart)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Cart", "Ordered products have been removed from the cart.");
                                    callback.onSuccess(orderId);
                                })
                                .addOnFailureListener(e -> Log.e("Cart", "Error removing ordered products from cart", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Cart", "Error querying Firestore", e));
    }

    public interface OrderDetailCallback {
        void onSuccess(Map<String, Object> orderDetails);
        void onFailure(Exception e);
    }

    public String generateOrderId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public interface OrderCallback {
        void onSuccess(String orderId);
        void onFailure(Exception e);
    }

    public interface UserDataCallback {
        void onResult(boolean isAvailable);
    }
}