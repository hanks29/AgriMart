package com.example.agrimart.viewmodel;

import android.app.Application;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserNameViewModel extends AndroidViewModel {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    private final MutableLiveData<String> originalUserName = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaveButtonEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isUserNameSaved = new MutableLiveData<>(false);


    public EditUserNameViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<String> getOriginalUserName() {
        return originalUserName;
    }

    public MutableLiveData<Boolean> isSaveButtonEnabled() {
        return isSaveButtonEnabled;
    }

    public MutableLiveData<Boolean> isUserNameSaved() {
        return isUserNameSaved;
    }

    public void loadUserInfo() {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            originalUserName.setValue(document.getString("fullName"));
                        }
                    }
                });
    }

    public void clearUserName() {
        originalUserName.setValue("");
    }

    public void saveUserNameToFirestore(String newUserName, TextView tvUserNameError) {
        if (newUserName.length() > 100) {
            tvUserNameError.setText("Tên người dùng không được vượt quá 100 ký tự!");
            tvUserNameError.setVisibility(View.VISIBLE);
            return;
        } else if (newUserName.trim().split("\\s+").length < 2) {
            tvUserNameError.setText("Vui lòng nhập cả họ và tên!");
            tvUserNameError.setVisibility(View.VISIBLE);
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("fullName", newUserName)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Tên người dùng đã được lưu!");
                        isUserNameSaved.setValue(true); // Đánh dấu tên người dùng đã được lưu
                    } else {
                        showToast("Có lỗi xảy ra khi lưu tên người dùng!");
                        isUserNameSaved.setValue(false); // Đánh dấu tên người dùng chưa được lưu
                    }
                });
    }

    public void updateSaveButtonState(String currentUserName) {
        boolean isEnabled = !currentUserName.equals(originalUserName.getValue()) && !currentUserName.isEmpty();
        isSaveButtonEnabled.setValue(isEnabled);
    }


    private void showToast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }
}
