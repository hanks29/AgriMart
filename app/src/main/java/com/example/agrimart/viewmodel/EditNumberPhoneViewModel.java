package com.example.agrimart.viewmodel;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNumberPhoneViewModel extends ViewModel {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Boolean> isPhoneNumberValid = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public EditNumberPhoneViewModel() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> getIsPhoneNumberValid() {
        return isPhoneNumberValid;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void validatePhoneNumber(String phone) {
        if (!phone.startsWith("0")) {
            isPhoneNumberValid.setValue(false); // Số điện thoại không hợp lệ
            errorMessage.setValue("Số điện thoại phải bắt đầu bằng 0.");

        } else if (phone.length() != 10){
            isPhoneNumberValid.setValue(false); // Vô hiệu hóa nút nếu không đủ 10 số
            errorMessage.setValue("Số điện thoại phải có 10 chữ số.");
        } else {
                isPhoneNumberValid.setValue(true); // Số điện thoại hợp lệ
                errorMessage.setValue(null); // Xóa thông báo lỗi
        }
    }

    public void saveNumberPhoneToFirestore(String phone, Runnable onSuccess, Runnable onFailure) {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("phoneNumber", phone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onSuccess.run(); // Gọi phương thức thành công
                    } else {
                        onFailure.run(); // Gọi phương thức thất bại
                    }
                });
    }
}
