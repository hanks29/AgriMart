package com.example.agrimart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.agrimart.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tạo một Handler để trì hoãn việc chuyển đổi activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Chuyển đến activity mới
                Intent intent = new Intent(MainActivity.this, Login_Register.class);
                startActivity(intent);
                finish(); // Kết thúc MainActivity để người dùng không quay lại nó
            }
        }, 2000); // Thời gian trì hoãn (2000ms = 2 giây)
    }
}
