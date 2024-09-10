package com.example.agrimart.View.PostProduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agrimart.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostProductPhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostProductPhotosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostProductPhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostPhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostProductPhotosFragment newInstance(String param1, String param2) {
        PostProductPhotosFragment fragment = new PostProductPhotosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    AppCompatButton button_post_product_continue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_product_photos, container, false);
        addControl(view);
        addEvents(view);
        return view;
    }

    void addControl(View v)
    {
        button_post_product_continue = v.findViewById(R.id.button_post_product_continue);
    }

    void addEvents(View v) {
        button_post_product_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một instance của Fragment mới
                Fragment newFragment = new PostProductPriceFragment();

                // Lấy FragmentManager và bắt đầu giao dịch
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Thay thế fragment hiện tại bằng fragment mới
                transaction.replace(R.id.fragmentPostProduct, newFragment);

                // Nếu bạn muốn quay lại fragment cũ khi nhấn nút "Back", hãy thêm giao dịch vào back stack
                transaction.addToBackStack(null);

                // Hoàn tất giao dịch
                transaction.commit();
            }
        });
    }

}