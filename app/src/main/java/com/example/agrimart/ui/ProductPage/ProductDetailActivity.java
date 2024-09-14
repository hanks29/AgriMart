package com.example.agrimart.ui.ProductPage;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.agrimart.R;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
    ImageButton btn_back;
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

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());

        ArrayList<SlideModel> imgList = new ArrayList<>();
        imgList.add(new SlideModel(R.drawable.banana, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.apple, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.banana, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.apple, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.banana, "", ScaleTypes.FIT));
        imgList.add(new SlideModel(R.drawable.apple, "", ScaleTypes.FIT));


        ImageSlider imageSlider = findViewById(R.id.product_image);
        imageSlider.setImageList(imgList, ScaleTypes.FIT);
    }
}