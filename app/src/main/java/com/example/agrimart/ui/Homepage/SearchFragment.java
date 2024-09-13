package com.example.agrimart.ui.Homepage;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agrimart.R;
import com.example.agrimart.SpacesItemDecoration;
import com.example.agrimart.data.adapter.CategoryAdapter;
import com.example.agrimart.data.adapter.CategoryAdapter1;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentSearchBinding.inflate(inflater,container,false);
        int spacingInPixels=getResources().getDimensionPixelSize(R.dimen.spacing);
        List<Category> categories=new ArrayList<>();
        categories.add(new Category(R.drawable.frash_fruits,"Frash Fruits\n" +
                "& Vegetable"));
        categories.add(new Category(R.drawable.frash_fruits,"Frash Fruits\n" +
                "& Vegetable"));
        categories.add(new Category(R.drawable.frash_fruits,"Frash Fruits\n" +
                "& Vegetable"));
        categories.add(new Category(R.drawable.frash_fruits,"Frash Fruits\n" +
                "& Vegetable"));
        categories.add(new Category(R.drawable.frash_fruits,"Frash Fruits\n" +
                "& Vegetable"));
        categories.add(new Category(R.drawable.frash_fruits,"Frash Fruits\n" +
                "& Vegetable"));
        categories.add(new Category(R.drawable.frash_fruits,"Frash Fruits\n" +
                "& Vegetable"));
        CategoryAdapter1 categoryAdapter1=new CategoryAdapter1(categories);
        binding.rvCategories.setAdapter(categoryAdapter1);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.rvCategories.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        return binding.getRoot();
    }
}