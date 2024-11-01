package com.example.agrimart.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpViewModel extends AndroidViewModel {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private MutableLiveData<Boolean> signUpSuccess;
    private MutableLiveData<Boolean> emailVerificationSent;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        signUpSuccess = new MutableLiveData<>();
        emailVerificationSent = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public MutableLiveData<Boolean> getEmailVerificationSent() {
        return emailVerificationSent;
    }

    public void signUpWithEmail(String email, String password, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sendEmailVerification(user);
                            saveUserToFirestore(user, fullName);
                        }
                        signUpSuccess.setValue(true);
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplication(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            signUpSuccess.setValue(false);
                        }
                    }
                });
    }

    public void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        emailVerificationSent.setValue(true);
                    } else {
                        emailVerificationSent.setValue(false);
                    }
                });
    }

    public void saveUserToFirestore(FirebaseUser firebaseUser, String fullName) {
        firestore.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String role = "customer";
                    if (documentSnapshot.exists() && documentSnapshot.contains("role")) {
                        role = documentSnapshot.getString("role");
                    }

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", firebaseUser.getUid());
                    userMap.put("role", role);
                    userMap.put("fullName", fullName);
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
                            })
                            .addOnFailureListener(e -> {
                            });
                })
                .addOnFailureListener(e -> {
                });
    }

}