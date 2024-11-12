package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.ProductCart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartFragmentViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private String userId;

    // Constructor
    public CartFragmentViewModel() {
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    // Hàm để lấy danh sách các StoreCart theo userId, sắp xếp theo ngày
    public void getStoreCartsByUserId(final OnDataFetchedListener listener) {
        userId = auth.getCurrentUser().getUid();

        firestore.collection("cart")
                .whereEqualTo("userId", userId) // Tìm các StoreCart có userId trùng với tham số
                .orderBy("updatedAt", Query.Direction.DESCENDING) // Sắp xếp theo updatedAt giảm dần
                .get() // Lấy dữ liệu
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Cart> storeCartList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Cart storeCart = document.toObject(Cart.class);
                            if (storeCart != null) {
                                storeCartList.add(storeCart);
                            }
                        }
                        // Sau khi lấy danh sách StoreCart, tiếp tục truy vấn thông tin sản phẩm tương ứng
                        fetchProductDetailsAndStore(storeCartList, listener);
                    } else {
                        Log.e("CartFragmentViewModel", "Failed to fetch data:" + task.getException().getMessage());
                    }
                });
    }


    // Hàm để lấy thông tin sản phẩm từ productId
    private void fetchProductDetailsAndStore(List<Cart> storeCartList, final OnDataFetchedListener listener) {
        List<Cart> updatedStoreCartList = new ArrayList<>();
        int[] remainingRequests = {storeCartList.size()}; // Đếm số yêu cầu cần xử lý

        for (Cart storeCart : storeCartList) {
            List<Product> products = storeCart.getProducts(); // Lấy danh sách sản phẩm hiện có trong storeCart
            int[] productRequests = {products.size()}; // Đếm yêu cầu lấy thông tin cho từng sản phẩm

            for (Product product : products) {
                firestore.collection("products")
                        .document(product.getProduct_id())
                        .get()
                        .addOnCompleteListener(productTask -> {
                            if (productTask.isSuccessful()) {
                                DocumentSnapshot productDoc = productTask.getResult();
                                if (productDoc.exists()) {
                                    // Cập nhật thông tin trực tiếp vào đối tượng Product hiện có
                                    product.setName(productDoc.getString("name"));
                                    product.setPrice(productDoc.getDouble("price"));
                                    product.setImages((List<String>) productDoc.get("images"));
                                }
                            } else {
                                listener.onError("Lấy thông tin sản phẩm thất bại: " + productTask.getException().getMessage());
                            }

                            // Kiểm tra nếu tất cả sản phẩm đã được xử lý
                            productRequests[0]--;
                            if (productRequests[0] == 0) {
                                // Khi tất cả sản phẩm đã được lấy, tiến hành lấy thông tin cửa hàng
                                firestore.collection("users")  // Collection "users" chứa thông tin cửa hàng
                                        .document(storeCart.getStoreId())
                                        .get()
                                        .addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful()) {
                                                DocumentSnapshot storeDoc = userTask.getResult();
                                                if (storeDoc.exists()) {
                                                    storeCart.setStore_name(storeDoc.getString("store_name"));
                                                }
                                            } else {
                                                listener.onError("Lấy thông tin cửa hàng thất bại: " + userTask.getException().getMessage());
                                            }

                                            // Thêm storeCart đã cập nhật vào danh sách kết quả
                                            updatedStoreCartList.add(storeCart);

                                            // Giảm đếm remainingRequests và kiểm tra nếu tất cả storeCart đã hoàn thành
                                            remainingRequests[0]--;
                                            if (remainingRequests[0] == 0) {
                                                listener.onDataFetched(updatedStoreCartList);
                                            }
                                        });
                            }
                        });
            }
        }
    }


    public void updateProductQuantityInFirebase(String productId, int newQuantity) {
        userId = auth.getCurrentUser().getUid();

        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Cart storeCart = document.toObject(Cart.class);

                        if (storeCart != null) {
                            List<ProductCart> updatedProductList = new ArrayList<>();
                            boolean productFound = false;

                            for (Product product : storeCart.getProducts()) {
                                int quantity = product.getProduct_id().equals(productId) ? newQuantity : product.getQuantity();
                                updatedProductList.add(new ProductCart(product.getProduct_id(), quantity));

                                if (product.getProduct_id().equals(productId)) {
                                    productFound = true;
                                }
                            }

                            if (productFound) {
                                storeCart.setProductCart(updatedProductList);

                                firestore.collection("cart")
                                        .document(document.getId())
                                        .update("products", storeCart.getProductCart())
                                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Giỏ hàng đã được cập nhật."))
                                        .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi cập nhật giỏ hàng: " + e.getMessage()));
                            } else {
                                Log.e("Firebase", "Không tìm thấy sản phẩm trong giỏ hàng.");
                            }
                        }
                    } else {
                        Log.e("Firebase", "Không tìm thấy giỏ hàng hoặc có lỗi xảy ra: " + (task.getException() != null ? task.getException().getMessage() : ""));
                    }
                });
    }



    public void addProductToCart(String storeId, Product productToAdd) {
        userId = auth.getCurrentUser().getUid();

        // Query the user's cart based on userId and storeId
        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .whereEqualTo("storeId", storeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Retrieve the existing cart if available
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            Cart storeCart = document.toObject(Cart.class);

                            if (storeCart != null) {
                                List<Product> products = storeCart.getProducts();
                                List<ProductCart> productCarts = new ArrayList<>();

                                if (products == null) {
                                    products = new ArrayList<>(); // Initialize if null
                                }

                                // Check if the product already exists in the cart
                                boolean productExists = false;
                                for (Product product : products) {
                                    ProductCart pro = new ProductCart();
                                    pro.setProduct_id(product.getProduct_id());
                                    pro.setQuantity(product.getQuantity());
                                    if (product.getProduct_id().equals(productToAdd.getProduct_id())) {
                                        productExists = true;
                                        pro.setQuantity(product.getQuantity() + 1);
                                    }
                                    productCarts.add(pro);
                                }

                                // If the product doesn't exist, add it as a new product
                                if (!productExists) {
                                    ProductCart pro = new ProductCart();
                                    pro.setProduct_id(productToAdd.getProduct_id());
                                    pro.setQuantity(1);
                                    productCarts.add(pro);
                                }

                                storeCart.setProduct(products); // Update the cart with the new product list
                                String updatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                storeCart.setUpdatedAt(updatedAt); // Update the timestamp

                                // Update Firestore
                                firestore.collection("cart")
                                        .document(document.getId())
                                        .update(
                                                "products", productCarts,
                                                "updatedAt", updatedAt
                                        )
                                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Product has been added to the cart."))
                                        .addOnFailureListener(e -> Log.e("Firebase", "Error adding product to cart: " + e.getMessage()));
                            }
                        } else {
                            // Define a new cart
                            Map<String, Object> newCart = new HashMap<>();
                            newCart.put("userId", userId);
                            newCart.put("storeId", storeId);
                            String updatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                            newCart.put("updatedAt", updatedAt);

                            // Create the initial list of products
                            List<Map<String, Object>> productCarts = new ArrayList<>();
                            Map<String, Object> productData = new HashMap<>();
                            productData.put("product_id", productToAdd.getProduct_id());
                            productData.put("quantity", 1);
                            productCarts.add(productData);
                            newCart.put("products", productCarts);

                            // Add the new cart to Firestore
                            firestore.collection("cart")
                                    .add(newCart)
                                    .addOnSuccessListener(documentReference -> Log.d("Firebase", "New cart has been created."))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Error creating new cart: " + e.getMessage()));
                        }
                    } else {
                        Log.e("Firebase", "Error retrieving cart: " + task.getException());
                    }
                });
    }


    // Listener để callback khi lấy dữ liệu xong
    public interface OnDataFetchedListener {
        void onDataFetched(List<Cart> storeCarts);

        void onError(String errorMessage);
    }


}
