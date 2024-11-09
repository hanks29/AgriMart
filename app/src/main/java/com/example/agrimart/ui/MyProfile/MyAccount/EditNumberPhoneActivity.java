package com.example.agrimart.ui.MyProfile.MyAccount;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agrimart.R;
import com.example.agrimart.viewmodel.EditNumberPhoneViewModel;

public class EditNumberPhoneActivity extends AppCompatActivity {

    private EditNumberPhoneViewModel viewModel;
    private AppCompatButton btnUpdate;
    private EditText numberPhone;
    private ImageButton btn_back;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_number_phone);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(EditNumberPhoneViewModel.class);

        addControl();
        addEvent();

        // Theo dõi sự thay đổi trong biến errorMessage
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(error);
            } else {
                errorMessage.setVisibility(View.GONE);
            }
        });

        // Theo dõi sự thay đổi trong biến isPhoneNumberValid
        viewModel.getIsPhoneNumberValid().observe(this, isValid -> {
            btnUpdate.setEnabled(isValid);
            btnUpdate.setBackgroundColor(isValid ? ContextCompat.getColor(this, R.color.green) : ContextCompat.getColor(this, R.color.gray));
        });
    }

    void addControl() {
        btnUpdate = findViewById(R.id.btnUpdate);
        numberPhone = findViewById(R.id.user_phone);
        btn_back = findViewById(R.id.btn_back);
        errorMessage = findViewById(R.id.errorMessage);

        btnUpdate.setEnabled(false);

        numberPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.validatePhoneNumber(s.toString()); // Kiểm tra số điện thoại
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    void addEvent() {
        btn_back.setOnClickListener(v -> onBackPressed());
        btnUpdate.setOnClickListener(v -> {
            String phone = numberPhone.getText().toString().trim();
            viewModel.saveNumberPhoneToFirestore(phone,
                    () -> {
                        Toast.makeText(this, "Số điện thoại đã được lưu!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    },
                    () -> {
                        Toast.makeText(this, "Có lỗi xảy ra khi lưu số điện thoại!", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
