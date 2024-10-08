package com.example.agrimart.ui.Explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.R;
import com.example.agrimart.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageButton filterIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        filterIcon = view.findViewById(R.id.filter_icon);

        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        fragments.add(new CategoryFragment("Rau củ quả"));
        titles.add("Rau củ quả");
        fragments.add(new CategoryFragment("Trái cây"));
        titles.add("Trái cây");
        fragments.add(new CategoryFragment("Ngũ cốc và hạt"));
        titles.add("Ngũ cốc và hạt");
        fragments.add(new CategoryFragment("Gia vị"));
        titles.add("Gia vị");
        fragments.add(new CategoryFragment("Mật ong"));
        titles.add("Mật ong");
        fragments.add(new CategoryFragment("Trà"));
        titles.add("Trà");
        fragments.add(new CategoryFragment("Cây cảnh"));
        titles.add("Cây cảnh");
        fragments.add(new CategoryFragment("Khác"));
        titles.add("Khác");

        viewPagerAdapter = new ViewPagerAdapter(getActivity(), fragments, titles);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(titles.get(position))).attach();

        filterIcon.setOnClickListener(v -> showFilterMenu(v));

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