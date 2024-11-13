package com.example.agrimart.ui.PostProduct;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.agrimart.R;

public class YourProductListingsActivity extends AppCompatActivity {


    Fragment productPostListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_product_listings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            productPostListFragment = new YourProductListingsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_post_product, productPostListFragment);
            transaction.commit();
        }

        ImageButton btn_backPostList = findViewById(R.id.btn_backPostList);
        btn_backPostList.setOnClickListener(v -> {
            finish();
        });


    }


}
