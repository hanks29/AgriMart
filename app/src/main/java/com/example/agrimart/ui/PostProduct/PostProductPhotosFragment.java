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
import java.util.Objects;


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
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(4), uris -> {
                    if (!uris.isEmpty()) {
//                        imageUris.clear();
                        imageUris.addAll(uris);
                        if(uris.size()>0 && imageUris.size()>0){
                            Glide.with(this).load(imageUris.get(0)).into(binding.imageView5);
                            binding.frameLayout1.setVisibility(View.VISIBLE);
                        }
                        if(uris.size()>1 && imageUris.size()>1){
                            Glide.with(this).load(imageUris.get(1)).into(binding.imageView6);
                            binding.frameLayout2.setVisibility(View.VISIBLE);
                        }
                        if(uris.size()>2 && imageUris.size()>2){
                            Glide.with(this).load(imageUris.get(2)).into(binding.imageView7);
                            binding.frameLayout3.setVisibility(View.VISIBLE);
                        }
                        if(uris.size()>3 && imageUris.size()>3){
                            Glide.with(this).load(imageUris.get(3)).into(binding.imageView8);
                            binding.frameLayout4.setVisibility(View.VISIBLE);
                        }
                        binding.linearLayout.setVisibility(View.VISIBLE);

                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        binding.imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Objects.isNull(imageUris) || !imageUris.isEmpty() || imageUris.size()<4){
                    pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
                else{
                    Toast.makeText(getContext(), "Vui long xóa ảnh đã chọn để thêm ảnh mới", Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.frameLayout1.setVisibility(View.GONE);
                binding.imageButton3.setVisibility(View.GONE);

                if(!imageUris.isEmpty()){
                    imageUris.remove(0);
                }
                if(imageUris.isEmpty()){
                    binding.linearLayout.setVisibility(View.GONE);
                }
            }
        });
        binding.imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.frameLayout2.setVisibility(View.GONE);
                binding.imageButton4.setVisibility(View.GONE);

                if(!imageUris.isEmpty()){
                    imageUris.remove(1);
                }
                if(imageUris.isEmpty()){
                    binding.linearLayout.setVisibility(View.GONE);
                }
            }
        });
        binding.imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.frameLayout3.setVisibility(View.GONE);
                binding.imageButton5.setVisibility(View.GONE);

                if(!imageUris.isEmpty()){
                    imageUris.remove(2);
                }
                if(imageUris.isEmpty()){
                    binding.linearLayout.setVisibility(View.GONE);
                }
            }
        });
        binding.imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.imageView8.setVisibility(View.GONE);
                binding.imageButton6.setVisibility(View.GONE);


                if(!imageUris.isEmpty()){
                    imageUris.remove(3);
                }
                if(imageUris.isEmpty()){
                    binding.linearLayout.setVisibility(View.GONE);
                }
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
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragmentPostProduct, newFragment);

                transaction.addToBackStack(null);
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