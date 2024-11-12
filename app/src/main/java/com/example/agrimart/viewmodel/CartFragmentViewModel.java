package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.ProductCart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
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

    // Hàm để lấy danh sách các StoreCart theo userId
    public void getStoreCartsByUserId(final OnDataFetchedListener listener) {
        userId = auth.getCurrentUser().getUid();

        firestore.collection("cart")
                .whereEqualTo("userId", userId) // Tìm các StoreCart có userId trùng với tham số
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
                        listener.onError("Failed to fetch data: " + task.getException().getMessage());
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

        // Lấy tài liệu giỏ hàng của người dùng
        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0); // Lấy tài liệu đầu tiên
                            Cart storeCart = document.toObject(Cart.class);

                            if (storeCart != null) {
                                boolean productUpdated = false;
                                List<ProductCart> productCartList = new ArrayList<>();
                                // Duyệt qua các sản phẩm trong giỏ hàng và cập nhật số lượng
                                for (Product product : storeCart.getProducts()) {
                                    ProductCart pro = new ProductCart();
                                    pro.setProduct_id(product.getProduct_id());
                                    pro.setQuantity(product.getQuantity());
                                    if (product.getProduct_id().equals(productId)) {
                                        pro.setQuantity(newQuantity); // Cập nhật quantity của sản phẩm
                                        productUpdated = true;
                                    }
                                    productCartList.add(pro);
                                }
                                storeCart.setProductCart(productCartList);

                                if (productUpdated) {
                                    // Cập nhật lại mảng sản phẩm trong tài liệu
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
                            Log.e("Firebase", "Không tìm thấy giỏ hàng cho người dùng.");
                        }
                    } else {
                        Log.e("Firebase", "Lỗi khi lấy giỏ hàng: " + task.getException());
                    }
                });
    }


    public void addProductToCart(String storeId, Product productToAdd) {
        userId = auth.getCurrentUser().getUid();

        // Truy vấn giỏ hàng của người dùng dựa trên userId và storeId
        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .whereEqualTo("storeId", storeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Lấy giỏ hàng nếu có
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            Cart storeCart = document.toObject(Cart.class);

                            if (storeCart != null) {
                                List<Product> products = storeCart.getProducts();
                                List<ProductCart> productCarts = new ArrayList<>();

                                if (products == null) {
                                    products = new ArrayList<>(); // Khởi tạo nếu là null
                                }

                                // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
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

                                // Nếu sản phẩm chưa có, thêm sản phẩm mới
                                if (!productExists) {
                                    ProductCart pro = new ProductCart();
                                    pro.setProduct_id(productToAdd.getProduct_id());
                                    pro.setQuantity(1);
                                    productCarts.add(pro);
                                }



                                storeCart.setProduct(products); // Cập nhật lại giỏ hàng với danh sách sản phẩm mới
                                storeCart.setUpdatedAt(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())); // Cập nhật thời gian



                                // Cập nhật Firestore
                                firestore.collection("cart")
                                        .document(document.getId())
                                        .update(
                                                "products", productCarts,
                                                "updatedAt", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())
                                        )
                                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Sản phẩm đã được thêm vào giỏ hàng."))
                                        .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage()));
                            }
                        } else {
                            // Định nghĩa giỏ hàng mới
                            Map<String, Object> newCart = new HashMap<>();
                            newCart.put("userId", userId);
                            newCart.put("storeId", storeId);
                            newCart.put("updatedAt", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

                            // Tạo danh sách sản phẩm ban đầu
                            List<Map<String, Object>> productCarts = new ArrayList<>();
                            Map<String, Object> productData = new HashMap<>();
                            productData.put("product_id", productToAdd.getProduct_id());
                            productData.put("quantity", 1);
                            productCarts.add(productData);
                            newCart.put("products", productCarts);

                            // Cập nhật Firestore
                            firestore.collection("cart")
                                    .add(newCart)
                                    .addOnSuccessListener(documentReference -> Log.d("Firebase", "Giỏ hàng mới đã được tạo."))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi tạo giỏ hàng mới: " + e.getMessage()));

                        }
                    } else {
                        Log.e("Firebase", "Lỗi khi lấy giỏ hàng: " + task.getException());
                    }
                });
    }




    // Listener để callback khi lấy dữ liệu xong
    public interface OnDataFetchedListener {
        void onDataFetched(List<Cart> storeCarts);

        void onError(String errorMessage);
    }


}
