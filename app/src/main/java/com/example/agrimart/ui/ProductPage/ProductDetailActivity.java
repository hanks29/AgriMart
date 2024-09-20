package com.example.agrimart.ui.ProductPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.agrimart.R;
import com.example.agrimart.ui.Store.StoreActivity;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
    ImageButton btn_back, btnExpand;
    TextView description;
    boolean isExpanded = false;
    LinearLayout store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        description = findViewById(R.id.description);
        btnExpand = findViewById(R.id.btn_expand);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());
        store = findViewById(R.id.store);

        ArrayList<SlideModel> imgList = new ArrayList<>();
        imgList.add(new SlideModel(R.drawable.banana, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.apple, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.banana, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.apple, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.banana, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.apple, "", ScaleTypes.FIT));

        ImageSlider imageSlider = findViewById(R.id.product_image);
        imageSlider.setImageList(imgList, ScaleTypes.FIT);

        btnExpand.setOnClickListener(v -> {
            if (isExpanded) {
                description.setMaxLines(3);
                btnExpand.setImageResource(R.drawable.down);
            } else {
                description.setMaxLines(Integer.MAX_VALUE);
                btnExpand.setImageResource(R.drawable.arrowhead_up);
            }
            isExpanded = !isExpanded;
        });

        store.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, StoreActivity.class);
            startActivity(intent);
        });
    }
}