package com.example.agrimart.ui.PostProduct;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.agrimart.R;
import com.example.agrimart.adapter.CategoryAdapter;
import com.example.agrimart.data.model.Category;
import com.example.agrimart.data.model.PostProduct;
import com.example.agrimart.data.model.ProductRequest;
import com.example.agrimart.databinding.FragmentPostProductPhotosBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



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
    FragmentPostProductPhotosBinding binding;
    private List<Uri> imageUris=new ArrayList<>();
    private List<Category> categories=new ArrayList<>();
    private CategoryAdapter categoryAdapter;
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
        binding = FragmentPostProductPhotosBinding.bind(view);
        addControl(view);
        addEvents(view);

        ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uris -> {
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (!uris.isEmpty()) {
                        if(uris.size()>0){
                            Glide.with(this).load(uris.get(0)).into(binding.imageView3);
                        }
                        if(uris.size()>1){
                            Glide.with(this).load(uris.get(1)).into(binding.imageView6);
                        }
                        if(uris.size()>2){
                            Glide.with(this).load(uris.get(2)).into(binding.imageView7);
                        }
                        if(uris.size()>3){
                            Glide.with(this).load(uris.get(3)).into(binding.imageView8);
                        }
                        binding.linearLayout.setVisibility(View.VISIBLE);
                        imageUris.clear();
                        imageUris.addAll(uris);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        binding.imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
        loadCategory();
        categoryAdapter = new CategoryAdapter(categories);
        binding.rvCategories.setAdapter(categoryAdapter);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));



        return binding.getRoot();

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
                if(categoryAdapter.category==null){
                    Toast.makeText(getContext(), "Vui long chon danh muc", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProductRequest product = new ProductRequest(binding.edtName.getText().toString(),binding.edtDes.getText().toString(),categoryAdapter.category.getId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("postProduct",product);
                bundle.putParcelableArrayList("imageUris", (ArrayList<Uri>) imageUris);
                newFragment.setArguments(bundle);
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

    void loadCategory(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .orderBy("id")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Category> categoryList = task.getResult().toObjects(Category.class);
                        categories.addAll(categoryList);
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("HomeFragmentViewModel", "Error getting documents: ", task.getException());
                    }
                });
    }

}