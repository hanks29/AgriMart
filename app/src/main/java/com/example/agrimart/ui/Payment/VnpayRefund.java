package com.example.agrimart.ui.Payment;

import com.example.agrimart.data.API.Config_VNPAY;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.util.Log;
import org.json.JSONObject;

public class VnpayRefund {

    public static String createRefundRequest(String txnRef, String transactionNo, int amount, String transactionDate, String orderInfo, String createBy) throws Exception {
        String vnp_RequestId = UUID.randomUUID().toString();
        String vnp_Version = Config_VNPAY.VNP_VERSION;
        String vnp_Command = Config_VNPAY.VNP_COMMAND_REFUND;
        String vnp_TmnCode = Config_VNPAY.VNP_TMNCODE;
        String vnp_TransactionType = "02";
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String vnp_IpAddr = InetAddress.getLocalHost().getHostAddress();
        String vnp_HashSecret = Config_VNPAY.VNP_HASH_SECRET;
        String formattedAmount = String.valueOf(amount * 100);

        //checksum
        String data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" + vnp_TransactionType + "|" + txnRef + "|" + formattedAmount + "|" + transactionNo + "|" + transactionDate + "|" + createBy + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + orderInfo;

        //vnp_SecureHash
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, data);

        // tạo parameters map
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_Amount", formattedAmount);
        vnp_Params.put("vnp_TransactionNo", transactionNo);
        vnp_Params.put("vnp_TransactionDate", transactionDate);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_CreateBy", createBy);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        // Create JSON body from vnp_Params
        JSONObject jsonBody = new JSONObject(vnp_Params);

        Log.d("VnpayRefund", "Refund Request: " + jsonBody.toString());

        // gủi request đến VNPAY
        URL url = new URL(Config_VNPAY.VNPAY_SANDBOX_REFUND_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        OutputStream os = connection.getOutputStream();
        os.write(jsonBody.toString().getBytes("UTF-8"));
        os.close();

        //Nhận response từ VNPAY
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            return "Lỗi: " + responseCode;
        }
    }

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