// SignUpViewModel.java
package com.example.agrimart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agrimart.data.model.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SignUpViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Boolean> signUpSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private String verificationId;

    public SignUpViewModel() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void sendOtp(String phoneNumber, String password) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(null)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential, phoneNumber, password);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        errorMessage.setValue("Verification failed: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        SignUpViewModel.this.verificationId = verificationId;
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOtp(String otp, String phoneNumber, String password) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential, phoneNumber, password);
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String phoneNumber, String password) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            User userData = new User(
                                    user.getUid(),
                                    "buyer",
                                    null, // fullName
                                    null, // email
                                    phoneNumber,
                                    password,
                                    null, // address
                                    null, // storeName
                                    null, // storeAddress
                                    null, // storeImage
                                    0.0, // storeRating
                                    null, // paymentAccount
                                    new Date(),
                                    new Date()
                            );
                            firestore.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> signUpSuccess.setValue(true))
                                    .addOnFailureListener(e -> errorMessage.setValue("Sign-up failed. Please try again."));
                        } else {
                            errorMessage.setValue("Sign-up failed. Please try again.");
                        }
                    } else {
                        errorMessage.setValue("Verification failed. Please try again.");
                    }
                });
    }
}