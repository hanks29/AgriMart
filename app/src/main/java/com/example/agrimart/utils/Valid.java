package com.example.agrimart.utils;

import android.widget.TextView;
import android.util.Patterns;

public class Valid {
    public static boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !password.isEmpty() && password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*");
    }

    public static boolean isValidName(String name) {
        return !name.isEmpty() && name.length() >= 3 && !name.matches(".*\\d.*");
    }

    public static boolean isPasswordMatching(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
