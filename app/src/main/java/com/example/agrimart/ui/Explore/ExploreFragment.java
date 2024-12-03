package com.example.agrimart.ui.Explore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ViewPagerAdapter;
import com.example.agrimart.databinding.FragmentExploreBinding;
import com.example.agrimart.ui.Cart.CartFragment;
import com.example.agrimart.ui.Homepage.HomeFragment;
import com.example.agrimart.ui.Homepage.SearchActivity;
import com.example.agrimart.ui.MyProfile.MyProfileFragment;
import com.example.agrimart.ui.Notification.NotificationFragment;
import com.example.agrimart.viewmodel.CategoryViewModel;
import com.example.agrimart.viewmodel.ExploreFragmentViewModel;
import com.example.agrimart.viewmodel.SharedViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton filterIcon;
    private FragmentExploreBinding binding;
    private ExploreFragmentViewModel viewModel;
    private String categoryID;
    private SharedViewModel sharedViewModel;

    public ExploreFragment(String categoryID) {
        this.categoryID = categoryID;
    }
    public ExploreFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);
        viewModel = new ViewModelProvider(this).get(ExploreFragmentViewModel.class);
        binding.setViewmodel(viewModel);
        View view = binding.getRoot();

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        filterIcon = view.findViewById(R.id.filter_icon);

        viewModel.getData();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            List<Fragment> fragments = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            int selectedCategoryIndex = -1; // Lưu chỉ số category được chọn (nếu có)

            // Kiểm tra nếu có categoryID và chọn category tương ứng
            if (categoryID != null && !categoryID.isEmpty()) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId().equals(categoryID)) {
                        selectedCategoryIndex = i;  // Ghi nhận chỉ số category được chọn
                        break;
                    }
                }
            }

            // Thêm các fragment cho tất cả các category vào ViewPager
            for (int i = 0; i < categories.size(); i++) {
                fragments.add(new CategoryFragment(categories.get(i).getId()));
                titles.add(categories.get(i).getName());
            }

            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity(), fragments, titles);
            viewPager.setAdapter(viewPagerAdapter);

            // Nếu có categoryID, chọn tab tương ứng
            if (selectedCategoryIndex != -1) {
                viewPager.setCurrentItem(selectedCategoryIndex);  // Chọn category tương ứng trong ViewPager
            }

            new TabLayoutMediator(tabLayout, viewPager, (tab, pos) -> tab.setText(titles.get(pos))).attach();

            filterIcon.setOnClickListener(this::showFilterMenu);
        });

        binding.searchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        binding.searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    private void showFilterMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::filterMenuItemSelected);
        popup.show();
    }

    private boolean filterMenuItemSelected(MenuItem item) {
        String sortOrder = null;
        if (item.getItemId() == R.id.filter_lowest_price) {
            sortOrder = "lowest_price";
            Log.d("ExploreFragment", "Sort order set to: " + sortOrder);
        } else if (item.getItemId() == R.id.filter_highest_price) {
            sortOrder = "highest_price";
        } else if (item.getItemId() == R.id.filter_latest) {
            sortOrder = "latest";
        } else if (item.getItemId() == R.id.filter_oldest) {
            sortOrder = "oldest";
        } else {
            return false;
        }
        sharedViewModel.setSortOrder(sortOrder);
        return true;
    }

}