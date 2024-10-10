package com.example.agrimart.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.agrimart.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignInViewModel extends AndroidViewModel {
    private static final String TAG = "SignInViewModel";
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    public MutableLiveData<FirebaseUser> userLiveData;
    private SharedPreferences sharedPreferences;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userLiveData = new MutableLiveData<>();
        sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    public void signInWithPhoneNumber(String phoneNumber) {
        firestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        userLiveData.setValue(firebaseUser);
                    } else {
                        userLiveData.setValue(null);
                    }
                });
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("userId", firebaseUser.getUid());
                            userMap.put("role", "customer");
                            if (firebaseUser.getDisplayName() != null) {
                                userMap.put("fullName", firebaseUser.getDisplayName());
                            }
                            if (firebaseUser.getEmail() != null) {
                                userMap.put("email", firebaseUser.getEmail());
                            }
                            if (firebaseUser.getPhoneNumber() != null) {
                                userMap.put("phoneNumber", firebaseUser.getPhoneNumber());
                            }
                            userMap.put("createdAt", new Date());
                            userMap.put("updatedAt", new Date());

                            firestore.collection("users").document(firebaseUser.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        userLiveData.setValue(firebaseUser);
                                        saveUserToPreferences(firebaseUser);
                                    })
                                    .addOnFailureListener(e -> {
                                        userLiveData.setValue(null);
                                    });

                        } else {
                            userLiveData.setValue(null);
                        }
                    } else {
                        userLiveData.setValue(null);
                    }
                });
    }

    private void saveUserToPreferences(FirebaseUser user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", user.getUid());
        editor.putString("user_name", user.getDisplayName());
        editor.putString("user_email", user.getEmail());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }
}