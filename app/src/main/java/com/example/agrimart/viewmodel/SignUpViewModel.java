package com.example.agrimart.viewmodel;

import android.telephony.SmsManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignUpViewModel extends ViewModel {
    private static final String TAG = "SignUpViewModel";
    private final MutableLiveData<Boolean> otpSentSuccess = new MutableLiveData<>();
    private String otpValue;

    public LiveData<Boolean> getOtpSentSuccess() {
        return otpSentSuccess;
    }

}
