package com.example.agrimart.ui.Explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ViewPagerAdapter;
import com.example.agrimart.databinding.FragmentExploreBinding;
import com.example.agrimart.viewmodel.ExploreFragmentViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageButton filterIcon;
    private FragmentExploreBinding binding;
    private ExploreFragmentViewModel viewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_explore,container,false);
        viewModel=new ViewModelProvider(this).get(ExploreFragmentViewModel.class);
        binding.setViewmodel(viewModel);
        View view = binding.getRoot();

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        filterIcon = view.findViewById(R.id.filter_icon);

        binding.setViewmodel(viewModel);
        viewModel.getData();


        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            Log.d("khanhmfemf", "onCreateView: " + categories.size());
            List<Fragment> fragments = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            for (int i = 0; i < categories.size(); i++) {
                fragments.add(new CategoryFragment(categories.get(i).getName()));
                titles.add(categories.get(i).getName());
            }
            viewPagerAdapter = new ViewPagerAdapter(requireActivity(), fragments, titles);
            viewPager.setAdapter(viewPagerAdapter);
            Log.d("khanhmfemf", "hi: " + viewPager.getAdapter().getItemCount());
            new TabLayoutMediator(tabLayout,viewPager,(tab,pos)->{
                tab.setText(titles.get(pos));
                Log.d("khanhmfemf", "onCreateView: " + tab.getTabLabelVisibility());

            }).attach();
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(titles.get(position))).attach();

            filterIcon.setOnClickListener(v -> showFilterMenu(v));
        });

        return view;
    }

    private void showFilterMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filter_menu, popup.getMenu());
        //popup.setOnMenuItemClickListener(this::onFilterItemSelected);
        popup.show();
    }

//    private boolean onFilterItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.filter_option1)
//            return true;
//        } else if (itemId == R.id.filter_option2) {
//            return true;
//        } else if (itemId == R.id.filter_option3) {
//            return true;
//        } else if (itemId == R.id.filter_option4) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}