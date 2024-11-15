package com.example.agrimart.ui.Explore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.FragmentCategoryBinding;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.CategoryViewModel;
import com.example.agrimart.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private String categoryID;
    private CategoryViewModel viewModel;
    private ProductAdapter productAdapter;
    private FragmentCategoryBinding binding; // Sử dụng binding
    private SharedViewModel sharedViewModel;

    public CategoryFragment(String categoryID) {
        this.categoryID = categoryID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng ViewBinding
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Thiết lập RecyclerView
        RecyclerView recyclerView = binding.recyclerViewCategory;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels)); // Thêm ItemDecoration chỉ một lần

        // Khởi tạo adapter rỗng ban đầu
        List<Product> prod = new ArrayList<>();
        productAdapter = new ProductAdapter(prod, product -> {
            // Xử lý khi người dùng nhấp vào sản phẩm
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("product", (Parcelable) product);
            intent.putExtra("storeId", product.getStoreId());
            startActivity(intent);
        });
        recyclerView.setAdapter(productAdapter);

        // Khởi tạo ViewModel và lấy dữ liệu
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getProductsByCategory(categoryID);  // Gọi phương thức lấy dữ liệu từ ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Quan sát dữ liệu và cập nhật adapter
        viewModel.products.observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                // Cập nhật adapter với dữ liệu mới
                productAdapter.updateProducts(products);
                // Thiết lập item decoration (căn lề, padding, ...)
            }
        });

        sharedViewModel.getSortOrder().observe(getViewLifecycleOwner(), sortOrder -> {
            Log.d("CategoryFragment", "Observed sortOrder: " + sortOrder);
            if (sortOrder != null) {
                viewModel.setSortOrder(sortOrder);
                viewModel.applySort(); // Áp dụng sắp xếp dữ liệu khi sortOrder thay đổi
            }
        });


        return view;
    }
}
