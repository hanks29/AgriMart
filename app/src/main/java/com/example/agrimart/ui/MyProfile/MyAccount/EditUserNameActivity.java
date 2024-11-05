package com.example.agrimart.ui.MyProfile.MyAccount;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.agrimart.R;
import com.example.agrimart.viewmodel.EditUserNameViewModel;

public class EditUserNameActivity extends AppCompatActivity {

    private EditUserNameViewModel viewModel;
    private ImageButton btnBack;
    private ImageView userErase;
    private TextView userText, txt_luu, tvUserNameError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user_name);

        viewModel = new ViewModelProvider(this).get(EditUserNameViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        loadUserInfo();
        addEvent();
    }

    void addControl() {
        btnBack = findViewById(R.id.btn_back);
        userErase = findViewById(R.id.my_user_erase);
        userText = findViewById(R.id.user_text);
        txt_luu = findViewById(R.id.txt_luu);
        tvUserNameError = findViewById(R.id.tvUserNameError);
    }

    void addEvent() {
        btnBack.setOnClickListener(v -> finish());
        userErase.setOnClickListener(v -> viewModel.clearUserName());
        txt_luu.setOnClickListener(v -> viewModel.saveUserNameToFirestore(userText.getText().toString().trim(), tvUserNameError));

        // Thêm TextWatcher để theo dõi sự thay đổi trong userText
        userText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.updateSaveButtonState(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        viewModel.getOriginalUserName().observe(this, userName -> {
            userText.setText(userName);
            // Kiểm tra trạng thái của nút lưu
            viewModel.updateSaveButtonState(userText.getText().toString());
        });

        viewModel.isSaveButtonEnabled().observe(this, isEnabled -> {
            txt_luu.setEnabled(isEnabled);
            txt_luu.setTextColor(getResources().getColor(isEnabled ? R.color.green : R.color.gray));
        });

        // Quan sát trạng thái lưu tên người dùng
        viewModel.isUserNameSaved().observe(this, isSaved -> {
            if (isSaved) {
                setResult(RESULT_OK); // Đặt kết quả trả về
                finish(); // Kết thúc Activity
            }
        });
    }

    private void loadUserInfo() {
        viewModel.loadUserInfo();
    }
}
