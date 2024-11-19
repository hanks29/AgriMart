package com.example.agrimart.viewmodel;

import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Address;
import com.example.agrimart.data.model.GHNRequestFee;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.User;
import com.example.agrimart.data.model.ghn.Item;
import com.example.agrimart.data.service.GHNService;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private GHNService ghnService;
    public MutableLiveData<Integer> shippingFee = new MutableLiveData<>();
    public MutableLiveData<String> orderCode = new MutableLiveData<>();

    public CheckoutViewModel() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        ghnService = new GHNService();
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

    public void placeOrder(double totalPrice, String expectedDeliveryTime, double shippingFee, String paymentMethod, String shippingName, List<String> productIds, String address, String storeId, List<Product> products, OrderCallback callback) {
        String userId = auth.getCurrentUser().getUid();
        String orderId = generateOrderId();
        Date createdAt = new Date();

        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("productId", product.getProduct_id());
            productMap.put("productName", product.getName());
            productMap.put("quantity", product.getQuantity());
            productMap.put("price", product.getPrice());
            productList.add(productMap);
        }

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
        order.put("address", address);
        order.put("storeId", storeId);
        order.put("products", productList);

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

    public void createOrder(String address, String storeId) {
        String[] addressUser = address.split(",");
        if (addressUser.length < 4) {
            return;
        }

        String province = addressUser[3].trim();
        String district = addressUser[2].trim();
        String ward = addressUser[1].trim();
        String street = addressUser[0].trim();

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
                                db.collection("users").document(storeId)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                User shop = documentSnapshot.toObject(User.class);
                                                ghnService.getProvinceId(shop.getStoreAddress().getCity(), new GHNService.Callback<Integer>() {
                                                    @Override
                                                    public void onResponse(Integer pro) {
                                                        ghnService.getDistrictId(shop.getStoreAddress().getDistrict(), pro, new GHNService.Callback<Integer>() {
                                                            @Override
                                                            public void onResponse(Integer dis) {
                                                                ghnService.getWardCode(shop.getStoreAddress().getWard(), dis, new GHNService.Callback<String>() {
                                                                    @Override
                                                                    public void onResponse(String wa) {
                                                                        GHNRequestFee ghnRequestFee = new GHNRequestFee();
                                                                        ghnRequestFee.setServiceTypeId(2);
                                                                        ghnRequestFee.setFromWardCode(wa);
                                                                        ghnRequestFee.setFromDistrictId(dis);
                                                                        ghnRequestFee.setToWardCode(result);
                                                                        ghnRequestFee.setToDistrictId(districtId);
                                                                        ghnRequestFee.setWeight(1000);
                                                                        List<Item> items = new ArrayList<>();
                                                                        items.add(new Item("Khanh", 1, 100));
                                                                        ghnRequestFee.setItems(items);
                                                                        ghnService.getFeeOrder(ghnRequestFee, new GHNService.Callback<JsonNode>() {
                                                                            @Override
                                                                            public void onResponse(JsonNode rl) {
                                                                                int fee = extractFee(rl);
                                                                                shippingFee.setValue(fee);
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Exception e) {
                                                                                Log.d("REQUEST_BODY", e.getMessage());
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Exception e) {
                                                                        Log.d("REQUEST_BODY", "shop" + e.getMessage() + shop.getAddresses().get(0).getWard());
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onFailure(Exception e) {
                                                                Log.d("REQUEST_BODY", e.getMessage());
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        Log.d("REQUEST_BODY", e.getMessage());
                                                    }
                                                });

                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.d("REQUEST_BODY", "Shop: " + e.getMessage());
                                        });

                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("REQUEST_BODY", "Ward code: " + e.getMessage());
                            }

                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("REQUEST_BODY", "District code: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("REQUEST_BODY", "Province code: " + e.getMessage());
            }
        });
    }


    private Integer extractFee(JsonNode responseBody) {
        try {
            return responseBody.path("data").path("total").asInt();
        }catch (Exception e){
            throw new RuntimeException("Error while extracting fee");
        }
    }

    private String extractOrderCode(JsonNode responseBody) {
        try {
            return responseBody.path("data").path("order_code").asText();
        }catch (Exception e){
            throw new RuntimeException("Error while extracting order code");
        }
    }

    public void updateStatusOrder(String orderId,int fee){
        db.collection("orders").document(orderId)
                .update("shipping_fee", fee)
                .addOnSuccessListener(aVoid -> {
                    Log.d("REQUEST_BODY", "Order updated: ");
                })
                .addOnFailureListener(e -> {
                    Log.d("REQUEST_BODY", "Order updated: ");
                });
    }
}