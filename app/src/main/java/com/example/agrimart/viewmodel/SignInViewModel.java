package com.example.agrimart.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignInViewModel extends AndroidViewModel {
    private static final String TAG = "SignInViewModel";
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private SharedPreferences sharedPreferences;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    public void signInWithEmail(String email, String password, Runnable onSuccess) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        saveUserToFirestore(firebaseUser, onSuccess);
                    }
                }
            });
    }

    public void signInWithGoogle(AuthCredential credential, Runnable onSuccess) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        saveUserToFirestore(firebaseUser, onSuccess);
                    }
                }
            });
    }

    public void signInWithFacebook(AuthCredential credential, Runnable onSuccess) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        saveUserToFirestore(firebaseUser, onSuccess);
                    }
                }
            });
    }

    private void saveUserToFirestore(FirebaseUser firebaseUser, Runnable onSuccess) {
        firestore.collection("users").document(firebaseUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    String role = "customer";
                    if (documentSnapshot.contains("role")) {
                        role = documentSnapshot.getString("role");
                    }

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", firebaseUser.getUid());
                    userMap.put("role", role);
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
                            saveUserToPreferences(firebaseUser);
                            onSuccess.run();
                        });
                } else {
                    saveUserToPreferences(firebaseUser);
                    onSuccess.run();
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