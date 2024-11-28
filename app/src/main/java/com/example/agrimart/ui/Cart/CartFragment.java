package com.example.agrimart.ui.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.agrimart.R;
import com.example.agrimart.adapter.StoreCartAdapter;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.viewmodel.CartFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView rvCarts;
    private StoreCartAdapter storeCartAdapter;
    private CartFragmentViewModel viewModel;
    private TextView tv_total_price;
    private ImageButton btn_delete;
    ImageView checkboxAll;
    private Button btnCheckout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Cập nhật màu status bar
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        // Khởi tạo RecyclerView
        rvCarts = view.findViewById(R.id.rv_cart);
        rvCarts.setLayoutManager(new LinearLayoutManager(requireActivity()));
        tv_total_price = view.findViewById(R.id.tv_total_price);
        btn_delete = view.findViewById(R.id.btn_delete);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        // Khởi tạo ViewModel
        viewModel = new CartFragmentViewModel();
        checkboxAll = view.findViewById(R.id.checkbox_all);

        loadCartData();

        checkboxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxAll.getTag() == null || checkboxAll.getTag().equals("unchecked")) {
                    checkboxAll.setImageResource(R.drawable.checkbox_checked);
                    checkboxAll.setTag("checked");

                    // Đánh dấu tất cả sản phẩm trong giỏ hàng
                    if (storeCartAdapter != null) {
                        storeCartAdapter.setCheckedAll(true); // Cập nhật trạng thái tất cả s��n phẩm
                    }
                } else {
                    checkboxAll.setImageResource(R.drawable.checkbox_empty);
                    checkboxAll.setTag("unchecked");

                    // Bỏ chọn tất cả sản phẩm trong giỏ hàng
                    if (storeCartAdapter != null) {
                        storeCartAdapter.setCheckedAll(false); // Bỏ chọn tất cả sản phẩm
                    }
                }
            }
        });

        btn_delete.setOnClickListener(v -> {
            // Kiểm tra xem có ít nhất một sản phẩm được chọn trong giỏ hàng
            boolean isAnyProductSelected = false;
            if (storeCartAdapter != null) {
                for (Cart cart : storeCartAdapter.getStoreCartList()) {
                    for (Product product : cart.getProducts()) {
                        if (product.isChecked()) {
                            isAnyProductSelected = true;
                            break;
                        }
                    }
                    if (isAnyProductSelected) {
                        break;
                    }
                }
            }

            if (isAnyProductSelected) {
                // Nếu có sản phẩm được chọn, hiển thị dialog xác nhận xóa
                showDeleteDialog();
            } else {
                // Nếu không có sản phẩm nào được chọn, hiển thị thông báo yêu cầu chọn sản phẩm
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sản phẩm để xóa", Toast.LENGTH_SHORT).show();
            }
        });


        btnCheckout.setOnClickListener(v -> onCheckoutButtonClicked());

        return view;
    }

    private void showDeleteDialog() {
        ConfirmDeleteDialogFragment dialogFragment = new ConfirmDeleteDialogFragment(new ConfirmDeleteDialogFragment.OnDeleteConfirmedListener() {
            @Override
            public void onDeleteConfirmed() {
                viewModel.removeCheckedProductsOrCart(new CartFragmentViewModel.OnDeleteCompletedListener() {
                    @Override
                    public void onDeleteCompleted() {
                        if (checkboxAll.getTag() != null && checkboxAll.getTag().equals("checked")) {
                            if (storeCartAdapter != null) {
                                storeCartAdapter.clearData(); // Xóa tất cả sản phẩm trong adapter
                                onTotalPriceChanged(0); // Cập nhật tổng giá trị về 0
                                checkboxAll.setImageResource(R.drawable.checkbox_empty); // Đặt lại trạng thái checkbox
                                checkboxAll.setTag("unchecked"); // Đặt lại tag cho checkbox
                            }
                            Toast.makeText(requireContext(), "Giỏ hàng đã được xóa", Toast.LENGTH_SHORT).show();
                        } else {
                            // Nếu không chọn tất cả sản phẩm, gọi dialog xác nhận xóa như bình thường
                            loadCartData();
                            Toast.makeText(requireContext(), "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });

        // Hiển thị dialog xác nhận
        dialogFragment.show(getChildFragmentManager(), "ConfirmDeleteDialog");
    }


    public void onTotalPriceChanged(double totalPrice) {
        // Update the total price TextView with the new total
        tv_total_price.setText(String.format("Tổng tiền: %,.0f đ", totalPrice));
    }

    private void updateCheckoutButtonState() {
        if (storeCartAdapter != null) {
            boolean isAnyCartChecked = false;
            for (Cart cart : storeCartAdapter.getStoreCartList()) {
                if (cart.isChecked()) {
                    isAnyCartChecked = true;
                    break;
                }
            }
            btnCheckout.setEnabled(isAnyCartChecked);
        }
    }

    private void onCheckoutButtonClicked() {
        List<Product> selectedProducts = new ArrayList<>();
        List<String> productIds = new ArrayList<>();
        String storeName = "";
        boolean isSameStore = true;

        if (storeCartAdapter != null) {
            for (Cart cart : storeCartAdapter.getStoreCartList()) {
                for (Product product : cart.getProducts()) {
                    if (product.isChecked()) {
                        if (storeName.isEmpty()) {
                            storeName = cart.getStore_name();
                        } else if (!storeName.equals(cart.getStore_name())) {
                            isSameStore = false;
                            break;
                        }
                        selectedProducts.add(product);
                        productIds.add(product.getProduct_id());
                    }
                }
                if (!isSameStore) {
                    break;
                }
            }
        }

        if (!selectedProducts.isEmpty() && isSameStore) {
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            intent.putParcelableArrayListExtra("selectedProducts", new ArrayList<>(selectedProducts));
            intent.putStringArrayListExtra("productIds", new ArrayList<>(productIds));
            intent.putExtra("storeName", storeName);
            startActivity(intent);
        } else if (!isSameStore) {
            Toast.makeText(getContext(), "Chỉ có thể thanh toán sản phẩm của cùng một cửa hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCartData() {
        viewModel.getStoreCartsByUserId(new CartFragmentViewModel.OnDataFetchedListener() {
            @Override
            public void onDataFetched(List<Cart> storeCarts) {
                // Cập nhật lại dữ liệu cho RecyclerView
                if (storeCartAdapter != null) {
                    storeCartAdapter.updateData(storeCarts); // Cập nhật dữ liệu mới
                } else {
                    storeCartAdapter = new StoreCartAdapter(storeCarts, viewModel);
                    storeCartAdapter.setOnTotalPriceChangedListener(totalPrice -> onTotalPriceChanged(totalPrice));
                    rvCarts.setAdapter(storeCartAdapter);
                }

                boolean allChecked = true;
                for (Cart storeCart : storeCarts) {
                    if (!storeCart.isChecked()) {
                        allChecked = false;
                        break;
                    }
                }

                if (allChecked) {
                    checkboxAll.setImageResource(R.drawable.checkbox_checked);
                    checkboxAll.setTag("checked");
                } else {
                    checkboxAll.setImageResource(R.drawable.checkbox_empty);
                    checkboxAll.setTag("unchecked");
                }

                storeCartAdapter.setOnCheckAllListener(check -> {
                    if (check) {
                        checkboxAll.setImageResource(R.drawable.checkbox_checked);
                        checkboxAll.setTag("checked");
                    } else {
                        checkboxAll.setImageResource(R.drawable.checkbox_empty);
                        checkboxAll.setTag("unchecked");
                    }
                });

                storeCartAdapter.notifyTotalPriceChanged();
            }

            @Override
            public void onError(String errorMessage) {
                // Xử lý lỗi nếu không lấy được dữ liệu
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


}