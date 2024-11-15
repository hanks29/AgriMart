package com.example.agrimart.ui.Homepage;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
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
import com.example.agrimart.adapter.CarouselAdapter;
import com.example.agrimart.adapter.CategoryAdapter;
import com.example.agrimart.adapter.EndlessRecyclerViewScrollListener;
import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.data.model.Product;
import com.example.agrimart.databinding.FragmentHomeBinding;
import com.example.agrimart.ui.Explore.ExploreFragment;
import com.example.agrimart.ui.ProductPage.ProductDetailActivity;
import com.example.agrimart.viewmodel.HomeFragmentViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private ViewPager2 carouselViewPager;
    private CarouselAdapter carouselAdapter;
    private Handler carouselHandler;
    private Runnable carouselRunnable;

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
                .whereEqualTo("status", "available")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        totalPages= (task.getResult().size()/10)+1;
                    }
                });
    }


    @SuppressLint("NewApi")
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

        viewModel.getCategories();
//        viewModel.getFirstProducts();

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                CategoryAdapter categoryAdapter = new CategoryAdapter(categories, category -> {
                    // Khi chọn một category, chuyển đến ExploreFragment và chọn mục category
                    ExploreFragment newFragment = new ExploreFragment(category.getId());

                    // Thay đổi mục đã chọn trong BottomNavigationView
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
                    if (bottomNavigationView != null) {
                        bottomNavigationView.setSelectedItemId(R.id.category);
                    }

                    loadFragment(newFragment);
                });

                binding.rvCategories.setAdapter(categoryAdapter);
                binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
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
                intent.putExtra("product", (Parcelable) product);
                intent.putExtra("storeId", product.getStoreId());
                startActivity(intent);
            } catch (Exception e) {

            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvProducts.setLayoutManager(gridLayoutManager);
        binding.rvProducts.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        binding.rvProducts.setAdapter(productAdapter);






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
        setupCarousel();

        return view;
    }

    private void setupCarousel() {
        carouselViewPager = binding.carouselViewpager;
        carouselAdapter = new CarouselAdapter(getCarouselItems());
        carouselViewPager.setAdapter(carouselAdapter);

        carouselHandler = new Handler(Looper.getMainLooper());
        carouselRunnable = () -> {
            int currentItem = carouselViewPager.getCurrentItem();
            int nextItem = (currentItem + 1) % carouselAdapter.getItemCount();
            carouselViewPager.setCurrentItem(nextItem, true);
            carouselHandler.postDelayed(carouselRunnable, 10000);
        };
        carouselHandler.postDelayed(carouselRunnable, 10000);
    }

    private List<String> getCarouselItems() {
        List<String> items = new ArrayList<>();
        items.add("https://firebasestorage.googleapis.com/v0/b/agri-mart-2342e.appspot.com/o/categories%2Fgiavi.jpg?alt=media&token=3670aa28-c5ff-483c-bf9a-3325bf48ce9c");
        items.add("https://firebasestorage.googleapis.com/v0/b/agri-mart-2342e.appspot.com/o/categories%2Fgiavi.jpg?alt=media&token=3670aa28-c5ff-483c-bf9a-3325bf48ce9c");
        items.add("https://firebasestorage.googleapis.com/v0/b/agri-mart-2342e.appspot.com/o/categories%2Fgiavi.jpg?alt=media&token=3670aa28-c5ff-483c-bf9a-3325bf48ce9c");
        return items;
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
                .whereEqualTo("status", "available")
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
                .whereEqualTo("status", "available")
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
                        binding.seeAll1.setVisibility(View.GONE);

                    }
                });
    }


}