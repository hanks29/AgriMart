package com.example.agrimart.ui.MyProfile.MyAddress;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.agrimart.R;
import com.example.agrimart.adapter.AddressAdapter;
import com.example.agrimart.data.model.Address;
import com.example.agrimart.viewmodel.MyAddressViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private LinearLayout id_add_address;
    private MyAddressViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        ImageButton btn_back = findViewById(R.id.btn_back);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();
        setupViewModel();

        id_add_address = findViewById(R.id.id_add_address);
        id_add_address.setOnClickListener(v -> startNewAddressActivity());

        btn_back.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(this, new ArrayList<>(), address -> {
            // Access the Address object directly from the listener
            String addressId = address.getAddressId();  // Directly access the Address object
            Intent intent = new Intent(MyAddressActivity.this, EditAddressActivity.class);
            intent.putExtra("addressId", addressId);
            startActivityForResult(intent, NewAddressActivity.REQUEST_CODE_NEW_ADDRESS);
        });
        recyclerView.setAdapter(addressAdapter);
    }




    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MyAddressViewModel.class);
        viewModel.getAddresses().observe(this, addresses -> {
            if (addresses != null) {
                addressAdapter.updateAddresses(addresses);
            } else {
                Toast.makeText(this, "Bạn cần đăng nhập để xem địa chỉ của mình.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void startNewAddressActivity() {
        Intent intent = new Intent(MyAddressActivity.this, NewAddressActivity.class);
        startActivityForResult(intent, NewAddressActivity.REQUEST_CODE_NEW_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewAddressActivity.REQUEST_CODE_NEW_ADDRESS && resultCode == RESULT_OK) {
            viewModel.loadAddresses(); // Reload addresses if a new one was added
        }
    }
}
