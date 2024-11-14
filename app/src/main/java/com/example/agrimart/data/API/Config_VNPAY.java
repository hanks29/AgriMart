package com.example.agrimart.data.API;

public class Config_VNPAY {
    public static final String VNPAY_SANDBOX_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_TMNCODE = "06EGNZ93"; // Thay mã merchant ID của bạn
    public static final String VNP_HASH_SECRET = "CNUOQJRB30SG8MIJZBJW87HPIP036OAT";
    public static final String VNP_RETURN_URL = "http://localhost:8080/vnpay_return";
}
