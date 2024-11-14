package com.example.agrimart.utils;

import com.example.agrimart.data.API.Config_VNPAY;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnpayUtils {

    public static String createVnpayUrl(int amount, String orderInfo, String bankCode, String locale) throws Exception {
        String vnp_Version = Config_VNPAY.VNP_VERSION;
        String vnp_Command = Config_VNPAY.VNP_COMMAND;
        String vnp_TmnCode = Config_VNPAY.VNP_TMNCODE;
        String vnp_HashSecret = Config_VNPAY.VNP_HASH_SECRET;
        String vnp_ReturnUrl = Config_VNPAY.VNP_RETURN_URL;
        String vnp_IpAddr = "127.0.0.1";
        String vnp_CurrCode = "VND";
        String vnp_OrderType = "other";
        String vnp_TxnRef = UUID.randomUUID().toString();
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Tạo URL thanh toán
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_Locale", locale != null && !locale.isEmpty() ? locale : "vn");

        // Tạo checksum (vnp_SecureHash)
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, "UTF-8")).append("&");
                query.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, "UTF-8")).append("&");
            }
        }

        // Xóa ký tự `&`
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        // Tạo vnp_SecureHash bằng HMAC SHA512
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(URLEncoder.encode(vnp_SecureHash, "UTF-8"));

        return Config_VNPAY.VNPAY_SANDBOX_URL + "?" + query.toString();
    }

    //tạo HMAC SHA512
    public static String hmacSHA512(String key, String data) throws Exception {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA512");
        hmacSha512.init(secretKeySpec);
        byte[] hashBytes = hmacSha512.doFinal(data.getBytes("UTF-8"));
        StringBuilder result = new StringBuilder();
        for (byte b : hashBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
