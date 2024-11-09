package com.example.agrimart.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyWithPasswordViewModel extends AndroidViewModel {

    private FirebaseAuth auth;
    private MutableLiveData<Boolean> authenticationSuccess;

    public VerifyWithPasswordViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        authenticationSuccess = new MutableLiveData<>();
    }

    public LiveData<Boolean> isAuthenticationSuccessful() {
        return authenticationSuccess;
    }

    public void verifyPassword(String password, Context context) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Xác thực thành công", Toast.LENGTH_SHORT).show();
                    authenticationSuccess.setValue(true);
                } else {
                    Toast.makeText(context, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    authenticationSuccess.setValue(false);
                }
            });
        }
    }
}

