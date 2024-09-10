package com.example.agrimart.ui.PostProduct;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.agrimart.R;


public class PostProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);
        if (savedInstanceState == null) {
            // Create a new instance of the fragment
            PostProductPhotosFragment post = new PostProductPhotosFragment();

            // Get the FragmentManager
            FragmentManager fragmentManager = getSupportFragmentManager();

            // Begin a fragment transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add the fragment to the FrameLayout
            fragmentTransaction.add(R.id.fragmentPostProduct, post);

            // Commit the transaction
            fragmentTransaction.commit();
        }
    }
}