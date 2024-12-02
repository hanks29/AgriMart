package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.data.model.Cart;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.viewmodel.CartFragmentViewModel;

import java.util.List;

public class StoreCartAdapter extends RecyclerView.Adapter<StoreCartAdapter.StoreCartViewHolder> {

    private List<Cart> storeCartList;
    CartFragmentViewModel viewModel;
    ProductCartAdapter productAdapter;


    public StoreCartAdapter(List<Cart> storeCartList, CartFragmentViewModel viewModel) {
        this.storeCartList = storeCartList;
        this.viewModel = viewModel;  // Khởi tạo ViewModel một lần
    }


    public void updateData(List<Cart> newCarts) {
        this.storeCartList.clear();
        this.storeCartList.addAll(newCarts);
        notifyDataSetChanged();
    }

    // Trong StoreCartAdapter
    public void clearData() {
        storeCartList.clear(); // Xóa hết các sản phẩm trong giỏ hàng
        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật dữ liệu
    }


    @NonNull
    @Override
    public StoreCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_store, parent, false);
        return new StoreCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCartViewHolder holder, int position) {
        Cart storeCart = storeCartList.get(position);
        holder.tvStoreName.setText(storeCart.getStore_name());

        List<Product> products = storeCart.getProducts();
        productAdapter = new ProductCartAdapter(products, viewModel);

        productAdapter.setOnAllProductsCheckedListener(allChecked -> {
            storeCart.setChecked(allChecked);
            notifyTotalPriceChanged();
            notifyCheckAll();

            if (allChecked) {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_checked);
                holder.checkbox_store.setTag("checked");
            } else {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_empty);
                holder.checkbox_store.setTag("unchecked");
            }

            // Cập nhật lại trạng thái trong Firebase nếu cần
            viewModel.onlyUpdateStoreCheckedStatusInFirebase(storeCart.getStoreId(), allChecked);
        });

        productAdapter.setOnDecreaseButtonClickListener(l -> {
            notifyTotalPriceChanged();
        });

        holder.rvProducts.setAdapter(productAdapter);
        holder.rvProducts.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        // Cài đặt checkbox_store ban đầu dựa trên trạng thái của cửa hàng
        if (storeCart.isChecked()) {
            holder.checkbox_store.setImageResource(R.drawable.checkbox_checked);
            holder.checkbox_store.setTag("checked");
        } else {
            holder.checkbox_store.setImageResource(R.drawable.checkbox_empty);
            holder.checkbox_store.setTag("unchecked");
        }

        holder.checkbox_store.setOnClickListener(v -> {
            boolean newCheckedStatus = !(holder.checkbox_store.getTag() != null && holder.checkbox_store.getTag().equals("checked"));
            storeCart.setChecked(newCheckedStatus);

            for (Product product : products) {
                product.setChecked(newCheckedStatus);
            }

            if (newCheckedStatus) {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_checked);
                holder.checkbox_store.setTag("checked");

            } else {
                holder.checkbox_store.setImageResource(R.drawable.checkbox_empty);
                holder.checkbox_store.setTag("unchecked");
            }

            viewModel.updateStoreCheckedStatusInFirebase(storeCart.getStoreId(), newCheckedStatus);

            // Cập nhật lại UI của tất cả sản phẩm trong RecyclerView con
            productAdapter.notifyDataSetChanged();
            notifyTotalPriceChanged();
            notifyCheckAll();

        });

        productAdapter.setOnProductCheckedChangeListener(() -> {
            notifyTotalPriceChanged(); // Cập nhật tổng tiền
        });

    }


    // StoreCartAdapter.java
    public void setCheckedAll(boolean isChecked) {
        for (int i = 0; i < storeCartList.size(); i++) {
            Cart storeCart = storeCartList.get(i);
            storeCart.setChecked(isChecked);
            for (Product product : storeCart.getProducts()) {
                product.setChecked(isChecked);
            }
            viewModel.updateStoreCheckedStatusInFirebase(storeCart.getStoreId(), isChecked);
            notifyItemChanged(i); // Cập nhật từng item thay vì toàn bộ
        }
        notifyTotalPriceChanged();
        notifyCheckAll();
    }




    @Override
    public int getItemCount() {
        return storeCartList.size();
    }

    public void setStoreCarts(List<Cart> storeCarts) {
        this.storeCartList = storeCarts;
    }

    static class StoreCartViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoreName;
        RecyclerView rvProducts;
        ImageView checkbox_store;

        public StoreCartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            rvProducts = itemView.findViewById(R.id.rv_products);
            checkbox_store = itemView.findViewById(R.id.checkbox_store);
        }
    }

    private OnTotalPriceChangedListener totalPriceChangedListener;

    private OnCheckAllListener checkAllListener;

    // Giao diện cho listener thay đổi tổng tiền
    public interface OnTotalPriceChangedListener {
        void onTotalPriceChanged(double totalPrice);
    }

    public interface OnCheckAllListener {
        void onCheckAll(boolean checked);
    }

    // Thiết lập listener từ fragment
    public void setOnTotalPriceChangedListener(OnTotalPriceChangedListener listener) {
        this.totalPriceChangedListener = listener;
    }

    public void setOnCheckAllListener(OnCheckAllListener listener)
    {
        this.checkAllListener = listener;
    }

    // Phương thức cập nhật tổng tiền (bạn có thể gọi nó sau khi cập nhật sản phẩm)
    public void notifyTotalPriceChanged() {
        if (totalPriceChangedListener != null) {
            double totalPrice = calculateTotalPrice();
            totalPriceChangedListener.onTotalPriceChanged(totalPrice);
        }
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (Cart cart : storeCartList) {
            for (Product product : cart.getProducts())
            {
                if (product.isChecked()) {
                    total += cart.getTotalPrice(); // Tính tổng tiền chỉ của các giỏ hàng được chọn
                }
            }
        }
        return total;
    }


    public void notifyCheckAll(){
        if(checkAllListener != null) {
            boolean check = getCheckedAll();
            checkAllListener.onCheckAll(check);
        }
    }

    private boolean getCheckedAll()
    {
        for (Cart storeCart : storeCartList)
            if (!storeCart.isChecked())
                return false;
        return true;
    }

    public List<Cart> getStoreCartList() {
        return storeCartList;
    }

}
