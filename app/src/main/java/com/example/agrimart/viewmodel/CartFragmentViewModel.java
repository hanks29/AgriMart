package com.example.agrimart.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.Product;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.ProductCart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
                .whereEqualTo("userId", userId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Cart> storeCartList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Cart storeCart = document.toObject(Cart.class);
                            if (storeCart != null) {
                                storeCartList.add(storeCart);

                            }
                        }

                        fetchProductDetailsAndStore(storeCartList, listener);
                    } else {
                        Log.e("CartFragmentViewModel", "Failed to fetch data:" + task.getException().getMessage());
                    }
                });
    }



    // Hàm để lấy thông tin sản phẩm từ productId

    private void fetchProductDetailsAndStore(List<Cart> storeCartList, final OnDataFetchedListener listener) {
        // Khởi tạo danh sách với kích thước bằng storeCartList để đảm bảo thứ tự
        List<Cart> updatedStoreCartList = new ArrayList<>(storeCartList.size());
        for (int i = 0; i < storeCartList.size(); i++) {
            updatedStoreCartList.add(i, null); // Đảm bảo mỗi vị trí trong danh sách là null trước khi cập nhật
        }

        AtomicInteger remainingRequests = new AtomicInteger(storeCartList.size());  // Đếm số lượng yêu cầu còn lại

        for (int i = 0; i < storeCartList.size(); i++) {
            Cart storeCart = storeCartList.get(i);
            List<Product> products = storeCart.getProducts();
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
                                    product.setName(productDoc.getString("name"));
                                    product.setPrice(productDoc.getDouble("price"));
                                    product.setImages((List<String>) productDoc.get("images"));
                                    product.setStoreId(productDoc.getString("storeId"));
                                    product.setUnit(productDoc.getString("unit"));
                                }
                            } else {
                                listener.onError("Lấy thông tin sản phẩm thất bại: " + productTask.getException().getMessage());
                            }

                            // Sau khi tất cả sản phẩm của giỏ hàng đã được cập nhật
                            if (productRequests.decrementAndGet() == 0) {
                                firestore.collection("users")
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

                                            // Đặt storeCart đã được cập nhật vào đúng vị trí
                                            updatedStoreCartList.set(finalI, storeCart);

                                            // Sau khi tất cả các yêu cầu đã hoàn thành
                                            if (remainingRequests.decrementAndGet() == 0) {
                                                listener.onDataFetched(updatedStoreCartList); // Gọi listener khi tất cả dữ liệu đã được lấy về
                                            }
                                        });
                            }
                        });
            }
        }
    }





    public void updateProductQuantityInFirebase(String productId, String storeId, int newQuantity) {
        userId = auth.getCurrentUser().getUid();

        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .whereEqualTo("storeId", storeId)
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



    public void addProductToCart(String storeId, Product productToAdd, int quantity) {
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
                                        pro.setQuantity(product.getQuantity() + quantity);
                                    }
                                    productCarts.add(pro);
                                }

                                // If the product doesn't exist, add it as a new product
                                if (!productExists) {
                                    ProductCart pro = new ProductCart();
                                    pro.setProduct_id(productToAdd.getProduct_id());
                                    pro.setQuantity(quantity);
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
                            productData.put("quantity", quantity);
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

    public void updateProductCheckedStatusInFirebase(String productId, String storeId, boolean isChecked) {
        userId = auth.getCurrentUser().getUid();

        // Tạo Map để lưu trạng thái vào Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("checked", isChecked);

        // Truy vấn giỏ hàng theo userId và storeId
        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .whereEqualTo("storeId", storeId)
                .get()  // Lấy tất cả các tài liệu phù hợp
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Nếu có tài liệu giỏ hàng phù hợp, tiến hành cập nhật
                        DocumentSnapshot cartDoc = querySnapshot.getDocuments().get(0);
                        // Lấy mảng sản phẩm trong giỏ hàng
                        List<Map<String, Object>> products = (List<Map<String, Object>>) cartDoc.get("products");

                        // Kiểm tra và cập nhật sản phẩm trong mảng
                        for (Map<String, Object> product : products) {
                            if (product.get("product_id").equals(productId)) {
                                // Cập nhật trạng thái của sản phẩm trong mảng
                                product.put("checked", isChecked);
                                break; // Dừng lại sau khi cập nhật sản phẩm
                            }
                        }

                        // Cập nhật lại toàn bộ mảng products
                        Map<String, Object> updatedCart = new HashMap<>();
                        updatedCart.put("products", products);

                        // Cập nhật lại tài liệu giỏ hàng
                        firestore.collection("cart")
                                .document(cartDoc.getId())  // Lấy ID của tài liệu cart phù hợp
                                .update(updatedCart)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Cart", "Trạng thái checkbox đã được cập nhật");
                                    // Làm mới dữ liệu UI sau khi cập nhật
                                })
                                .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi cập nhật trạng thái checkbox", e));
                    } else {
                        // Không tìm thấy tài liệu giỏ hàng phù hợp
                        Log.e("Cart", "Không tìm thấy giỏ hàng của người dùng với storeId " + storeId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi truy vấn Firestore", e));
    }


    public void updateStoreCheckedStatusInFirebase(String storeId, boolean isChecked) {
        userId = auth.getCurrentUser().getUid();

        // Tạo Map để lưu trạng thái vào Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("checked", isChecked);  // Thêm trường 'checked' vào tài liệu giỏ hàng

        // Truy vấn giỏ hàng theo userId và storeId
        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .whereEqualTo("storeId", storeId)
                .get()  // Lấy tất cả các tài liệu phù hợp
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Duyệt qua tất cả các tài liệu giỏ hàng
                        for (DocumentSnapshot cartDoc : querySnapshot.getDocuments()) {
                            // Lấy mảng sản phẩm trong giỏ hàng
                            List<Map<String, Object>> products = (List<Map<String, Object>>) cartDoc.get("products");

                            if (products != null) {
                                // Cập nhật trạng thái checked cho tất cả sản phẩm trong giỏ hàng
                                for (Map<String, Object> product : products) {
                                    product.put("checked", isChecked);
                                }

                                // Cập nhật lại toàn bộ mảng products
                                Map<String, Object> updatedCart = new HashMap<>();
                                updatedCart.put("products", products);
                                updatedCart.put("checked", isChecked);  // Thêm trường checked vào giỏ hàng

                                // Cập nhật lại tài liệu giỏ hàng
                                firestore.collection("cart")
                                        .document(cartDoc.getId())  // Lấy ID của tài liệu cart phù hợp
                                        .update(updatedCart)
                                        .addOnSuccessListener(aVoid -> Log.d("Cart", "Trạng thái checkbox đã được cập nhật 1"))
                                        .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi cập nhật trạng thái checkbox", e));
                            }
                        }
                    } else {
                        // Không tìm thấy tài liệu giỏ hàng phù hợp
                        Log.e("Cart", "Không tìm thấy giỏ hàng của người dùng với storeId " + storeId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi truy vấn Firestore", e));
    }

    public void onlyUpdateStoreCheckedStatusInFirebase(String storeId, boolean isChecked) {
        userId = auth.getCurrentUser().getUid();

        // Tạo Map để lưu trạng thái vào Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("checked", isChecked);  // Thêm trường 'checked' vào tài liệu giỏ hàng

        // Truy vấn giỏ hàng theo userId và storeId
        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .whereEqualTo("storeId", storeId)
                .get()  // Lấy tất cả các tài liệu phù hợp
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Duyệt qua tất cả các tài liệu giỏ hàng
                        for (DocumentSnapshot cartDoc : querySnapshot.getDocuments()) {
                            // Lấy mảng sản phẩm trong giỏ hàng
                            List<Map<String, Object>> products = (List<Map<String, Object>>) cartDoc.get("products");

                            if (products != null) {

                                // Cập nhật lại toàn bộ mảng products
                                Map<String, Object> updatedCart = new HashMap<>();
                                //updatedCart.put("products", products);
                                updatedCart.put("checked", isChecked);  // Thêm trường checked vào giỏ hàng

                                // Cập nhật lại tài liệu giỏ hàng
                                firestore.collection("cart")
                                        .document(cartDoc.getId())  // Lấy ID của tài liệu cart phù hợp
                                        .update(updatedCart)
                                        .addOnSuccessListener(aVoid -> Log.d("Cart", "Trạng thái checkbox đã được cập nhật 1"))
                                        .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi cập nhật trạng thái checkbox", e));
                            }
                        }
                    } else {
                        // Không tìm thấy tài liệu giỏ hàng phù hợp
                        Log.e("Cart", "Không tìm thấy giỏ hàng của người dùng với storeId " + storeId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi truy vấn Firestore", e));
    }

    public void removeCheckedProductsOrCart(OnDeleteCompletedListener listener) {
        String userId = auth.getCurrentUser().getUid();

        // Truy vấn tất cả giỏ hàng của người dùng
        firestore.collection("cart")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Duyệt qua tất cả các giỏ hàng của người dùng
                        for (DocumentSnapshot cartDoc : querySnapshot.getDocuments()) {
                            List<Map<String, Object>> products = (List<Map<String, Object>>) cartDoc.get("products");

                            boolean allChecked = true;  // Biến kiểm tra tất cả sản phẩm có checked = true hay không

                            // Kiểm tra các sản phẩm có checked = true không
                            for (Map<String, Object> product : products) {
                                if (product.get("checked") == null || !(boolean) product.get("checked")) {
                                    allChecked = false;  // Nếu có sản phẩm không được chọn, set allChecked = false
                                    break;
                                }
                            }

                            if (allChecked) {
                                // Nếu tất cả sản phẩm đều được chọn (checked = true), xóa giỏ hàng
                                firestore.collection("cart")
                                        .document(cartDoc.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Cart", "Giỏ hàng đã bị xóa vì tất cả sản phẩm đều được chọn.");
                                            // Gọi callback khi việc xóa hoàn tất
                                            listener.onDeleteCompleted();
                                        })
                                        .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi xóa giỏ hàng", e));
                            } else {
                                // Nếu không phải tất cả sản phẩm đều được chọn, chỉ xóa những sản phẩm được chọn
                                List<Map<String, Object>> updatedProducts = new ArrayList<>();

                                // Giữ lại những sản phẩm chưa được chọn (checked = false)
                                for (Map<String, Object> product : products) {
                                    if (product.get("checked") == null || !(boolean) product.get("checked")) {
                                        updatedProducts.add(product);  // Giữ lại sản phẩm không được chọn
                                    }
                                }

                                // Cập nhật lại giỏ hàng với các sản phẩm chưa được chọn
                                Map<String, Object> updatedCart = new HashMap<>();
                                updatedCart.put("products", updatedProducts);

                                firestore.collection("cart")
                                        .document(cartDoc.getId())
                                        .update(updatedCart)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Cart", "Các sản phẩm được chọn đã được xóa khỏi giỏ hàng.");
                                            // Gọi callback khi việc xóa hoàn tất
                                            listener.onDeleteCompleted();
                                        })
                                        .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi xóa sản phẩm khỏi giỏ hàng", e));
                            }
                        }
                    } else {
                        // Nếu không có giỏ hàng cho người dùng
                        Log.e("Cart", "Không tìm thấy giỏ hàng của người dùng.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi truy vấn Firestore", e));
    }


    public interface OnDeleteCompletedListener {
        void onDeleteCompleted();
    }


    // Listener để callback khi lấy dữ liệu xong
    public interface OnDataFetchedListener {
        void onDataFetched(List<Cart> storeCarts);

        void onError(String errorMessage);
    }


}
