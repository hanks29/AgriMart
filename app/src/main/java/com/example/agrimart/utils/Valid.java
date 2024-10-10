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

    public static boolean validateInputs(String email, String password, String name, TextView tvEmailError, TextView tvPasswordError, TextView tvNameError) {
        boolean isValid = true;

        if (!isValidEmail(email)) {
            tvEmailError.setText("Email không hợp lệ");
            isValid = false;
        } else {
            tvEmailError.setText("");
        }

        if (!isValidPassword(password)) {
            tvPasswordError.setText("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa và số");
            isValid = false;
        } else {
            tvPasswordError.setText("");
        }

        if (!isValidName(name)) {
            tvNameError.setText("Tên phải có ít nhất 3 ký tự và không được chứa số");
            isValid = false;
        } else {
            tvNameError.setText("");
        }

        return isValid;
    }
}