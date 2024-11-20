package com.example.agrimart.ui.Payment;

import com.example.agrimart.data.API.Config_VNPAY;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnpayRefund {

    public static String createRefundRequest(String transactionNo, int amount, String transactionDate, String orderInfo, String createBy) throws Exception {
        String vnp_Version = Config_VNPAY.VNP_VERSION;
        String vnp_Command = "refund";
        String vnp_TmnCode = Config_VNPAY.VNP_TMNCODE;
        String vnp_HashSecret = Config_VNPAY.VNP_HASH_SECRET;
        String vnp_IpAddr = "127.0.0.1";
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Tạo tham số cho yêu cầu hoàn tiền
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TransactionNo", transactionNo);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_TransactionDate", transactionDate);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_CreateBy", createBy);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Tạo checksum (vnp_SecureHash)
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append("=").append(fieldValue).append("&");
                query.append(fieldName).append("=").append(fieldValue).append("&");
            }
        }

        // Xóa ký tự `&` cuối cùng
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        // Tạo vnp_SecureHash bằng HMAC SHA512
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        // Gửi yêu cầu POST đến VNPAY
        URL url = new URL(Config_VNPAY.VNPAY_SANDBOX_REFUND_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        OutputStream os = connection.getOutputStream();
        os.write(query.toString().getBytes("UTF-8"));
        os.close();

        // Nhận phản hồi từ VNPAY
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString(); // Trả về kết quả từ API
        } else {
            return "Lỗi: " + responseCode;
        }
    }

    // Hàm tạo HMAC SHA512
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