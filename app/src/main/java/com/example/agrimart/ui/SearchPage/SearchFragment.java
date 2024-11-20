package com.example.agrimart.ui.SearchPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

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
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.SearchViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();
    private RecyclerView rvResult;
    private TextView tvNoResult, tvSortText;
    private TabLayout tabLayoutSort;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rvResult = view.findViewById(R.id.rvResult);
        tvNoResult = view.findViewById(R.id.tvNoResult);
        tabLayoutSort = view.findViewById(R.id.tabLayout);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        rvResult.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvResult.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        productAdapter = new ProductAdapter(products, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("products", (Parcelable) product);
            intent.putExtra("storeId", product.getStoreId());
            startActivity(intent);
        });
        rvResult.setAdapter(productAdapter);

        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        searchViewModel.getFilteredProducts().observe(getViewLifecycleOwner(), filteredProducts -> {
            products.clear();
            if (filteredProducts != null && !filteredProducts.isEmpty()) {
                products.addAll(filteredProducts);
                tvNoResult.setVisibility(View.GONE);
                rvResult.setVisibility(View.VISIBLE);
            } else {
                tvNoResult.setVisibility(View.VISIBLE);
                rvResult.setVisibility(View.GONE);
            }
            productAdapter.notifyDataSetChanged();
        });

        tabLayoutSort.addTab(tabLayoutSort.newTab().setText("Liên quan nhất"));
        tabLayoutSort.addTab(tabLayoutSort.newTab().setText("Mới nhất"));
        tabLayoutSort.addTab(tabLayoutSort.newTab().setText("Cũ nhất"));
        tabLayoutSort.addTab(tabLayoutSort.newTab().setText("Giá thấp nhất"));
        tabLayoutSort.addTab(tabLayoutSort.newTab().setText("Giá cao nhất"));

        tabLayoutSort.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String selectedSortOption = tab.getText().toString();
            searchViewModel.sortProducts(selectedSortOption);
            rvResult.scrollToPosition(0);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }
    });
        return view;
    }


//    private void showDialog() {
//        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_sort, null);
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
//        bottomSheetDialog.setContentView(bottomSheetView);
//
//        RadioButton radioSortByRelated = bottomSheetView.findViewById(R.id.radioSortByRelated);
//        RadioButton radioSortByNewest = bottomSheetView.findViewById(R.id.radioSortByNewest);
//        RadioButton radioSortByOldest = bottomSheetView.findViewById(R.id.radioSortByOldest);
//        RadioButton radioSortByHighPrice = bottomSheetView.findViewById(R.id.radioSortByHighPrice);
//        RadioButton radioSortByLowPrice = bottomSheetView.findViewById(R.id.radioSortByLowPrice);
//
//
//        String selectedSort = tvSortText.getText().toString();
//        switch (selectedSort) {
//            case "Mới nhất":
//                radioSortByNewest.setChecked(true);
//                break;
//            case "Cũ nhất":
//                radioSortByOldest.setChecked(true);
//                break;
//            case "Giá cao nhất":
//                radioSortByHighPrice.setChecked(true);
//                break;
//            case "Giá thấp nhất":
//                radioSortByLowPrice.setChecked(true);
//                break;
//            case "Liên quan nhất":
//            default:
//                radioSortByRelated.setChecked(true);
//                break;
//        }
//
//        bottomSheetView.findViewById(R.id.tvSortByRelated).setOnClickListener(v -> {
//            tvSortText.setText("Liên quan nhất");
//            radioSortByRelated.setChecked(true);
//            searchViewModel.sortProducts("Liên quan nhất");
//            bottomSheetDialog.dismiss();
//        });
//
//        bottomSheetView.findViewById(R.id.tvSortByNewest).setOnClickListener(v -> {
//            tvSortText.setText("Mới nhất");
//            radioSortByNewest.setChecked(true);
//            searchViewModel.sortProducts("Mới nhất");
//            bottomSheetDialog.dismiss();
//        });
//
//        bottomSheetView.findViewById(R.id.tvSortByOldest).setOnClickListener(v -> {
//            tvSortText.setText("Cũ nhất");
//            radioSortByOldest.setChecked(true);
//            searchViewModel.sortProducts("Cũ nhất");
//            bottomSheetDialog.dismiss();
//        });
//
//        bottomSheetView.findViewById(R.id.tvSortByHighPrice).setOnClickListener(v -> {
//            tvSortText.setText("Giá cao nhất");
//            radioSortByHighPrice.setChecked(true);
//            searchViewModel.sortProducts("Giá cao nhất");
//            bottomSheetDialog.dismiss();
//        });
//
//        bottomSheetView.findViewById(R.id.tvSortByLowPrice).setOnClickListener(v -> {
//            tvSortText.setText("Giá thấp nhất");
//            radioSortByLowPrice.setChecked(true);
//            searchViewModel.sortProducts("Giá thấp nhất");
//            bottomSheetDialog.dismiss();
//        });
//
//        bottomSheetDialog.show();
//    }
}