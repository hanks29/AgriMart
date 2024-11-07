package com.example.agrimart.ui.Homepage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.adapter.CategoryAdapter;
import com.example.agrimart.adapter.EndlessRecyclerViewScrollListener;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.FragmentHomeBinding;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.HomeFragmentViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    ImageButton btnSearch;
    TextView search;
    HomeFragmentViewModel viewModel;
    FragmentHomeBinding binding;
    EndlessRecyclerViewScrollListener scrollListener;
    private int totalPages;
    private int currentPage = 1;
    private List<Product> products=new ArrayList<>();
    private DocumentSnapshot lastVisible;
    ProductAdapter productAdapter;
    private boolean isLoading = false;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        totalPages= (task.getResult().size()/10)+1;
                    }
                });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        binding.setViewmodel(viewModel);
        View view = binding.getRoot();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.green));
        }

        viewModel.getData();
        viewModel.getFirstProducts();

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
                binding.rvCategories.setAdapter(categoryAdapter);
                binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
                Log.d(TAG, "Categories loaded: " + categories.size());
            }
        });

//        viewModel.products.observe(getViewLifecycleOwner(), products -> {
//            if (products != null) {
//                productAdapter = new ProductAdapter(products, product -> {
//                    try {
//                        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
//                        intent.putExtra("product", product);
//                        intent.putExtra("storeId", product.getStoreId());
//                        startActivity(intent);
//                    } catch (Exception e) {
//
//                    }
//                });
//                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
//                binding.rvProducts.setLayoutManager(gridLayoutManager);
//                binding.rvProducts.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
//                binding.rvProducts.setAdapter(productAdapter);
//
//            }
//        });
        getFirstProducts();
        productAdapter= new ProductAdapter(products, product -> {
            try {
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("product", product);
                intent.putExtra("storeId", product.getStoreId());
                startActivity(intent);
            } catch (Exception e) {

            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvProducts.setLayoutManager(gridLayoutManager);
        binding.rvProducts.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        binding.rvProducts.setAdapter(productAdapter);


        binding.seeAll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMoreProducts();
            }
        });



        binding.scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int scrollViewHeight = binding.scrollView.getChildAt(0).getMeasuredHeight();
                int scrollViewVisibleHeight = binding.scrollView.getHeight();

                if (scrollY + scrollViewVisibleHeight >= scrollViewHeight) {

                    if (!isLoading) {
                        isLoading = true;
                        binding.seeAll1.setVisibility(View.VISIBLE);

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getMoreProducts();
                            }
                        }, 2000);
                    }
                }
            }
        });
        addControls(view);
        addEvents();


        return view;
    }

    private void addEvents() {
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            getActivity().startActivity(intent);
        });

        search.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });
    }

    private void addControls(View view) {
        btnSearch = view.findViewById(R.id.search_icon);
        search = view.findViewById(R.id.search_input);
    }

    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    public void getFirstProducts(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query first= db.collection("products")
                .orderBy("product_id")
                .limit(10);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
                        products.addAll(productList);
                        productAdapter.notifyDataSetChanged();

                        lastVisible=queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size()-1);
                    }
                });
    }

    public void getMoreProducts(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query next= db.collection("products")
                .orderBy("product_id")
                .startAfter(lastVisible)
                .limit(10);

        next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<Product> newList = queryDocumentSnapshots.toObjects(Product.class);

                        products.addAll(newList);
                        productAdapter.notifyDataSetChanged();
                        isLoading=false;

                        if(!newList.isEmpty()){
                            lastVisible=queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size()-1);
                        }
                        currentPage+=1;
                        if(currentPage==totalPages){
                            binding.seeAll1.setVisibility(View.GONE);
                        }

                    }
                });
    }


}