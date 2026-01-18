package com.buixuanphat.spot_on.service;

import com.buixuanphat.spot_on.dto.vnpay.VNPayRequest;
import com.buixuanphat.spot_on.entity.Invoice;
import com.buixuanphat.spot_on.entity.User;
import com.buixuanphat.spot_on.enums.Status;
import com.buixuanphat.spot_on.enums.Tier;
import com.buixuanphat.spot_on.exception.AppException;
import com.buixuanphat.spot_on.repository.InvoiceRepository;
import com.buixuanphat.spot_on.repository.UserRepository;
import com.buixuanphat.spot_on.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayService {

    @NonFinal
    @Value("${VNPay.vnp_TmnCode}")
    private String vnp_TmnCode;

    @NonFinal
    @Value("${VNPay.vnp_HashSecret}")
    private String vnp_HashSecret;

    @NonFinal
    @Value("${VNPay.vnp_PayUrl}")
    private String vnp_PayUrl;

    @NonFinal
    @Value("${VNPay.vnp_Returnurl}")
    private String vnp_Returnurl;

    InvoiceRepository invoiceRepository;

    UserRepository userRepository;

    @NonFinal
    Double sum = 0.0;

    public String createPaymentUrl(VNPayRequest req, HttpServletRequest servletReq) throws UnsupportedEncodingException {
        // 1. Khai báo các tham số cơ bản
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = String.valueOf(req.getTxnRef());
        String vnp_IpAddr = VNPayUtil.getIpAddress(servletReq);
        String vnp_TmnCode_Param = vnp_TmnCode;

        // Số tiền nhân 100
        long amount = (long) req.getAmount() * 100;

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnp_Version);
        vnpParams.put("vnp_Command", vnp_Command);
        vnpParams.put("vnp_TmnCode", vnp_TmnCode_Param);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpParams.put("vnp_OrderInfo", req.getOrderInfo());
        vnpParams.put("vnp_OrderType", req.getOrderType());
        vnpParams.put("vnp_ReturnUrl", vnp_Returnurl);
        vnpParams.put("vnp_IpAddr", vnp_IpAddr);
        vnpParams.put("vnp_Locale", (req.getLanguage() == null || req.getLanguage().isEmpty()) ? "vn" : req.getLanguage());

        if (req.getBankCode() != null && !req.getBankCode().isEmpty()) {
            vnpParams.put("vnp_BankCode", req.getBankCode());
        }

        // 2. Tạo ngày tạo và ngày hết hạn
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        vnpParams.put("vnp_CreateDate", now.format(formatter));
        vnpParams.put("vnp_ExpireDate", now.plusMinutes(15).format(formatter));

        // 3. Sắp xếp tham số
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        // 4. Build chuỗi HashData và QueryString
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);

            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build Hash (Dùng URLEncoder chuẩn 2.1.0)
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                // Build Query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // 5. Tạo chữ ký SecureHash
        String queryUrl = query.toString();
        String secureHash = VNPayUtil.hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + secureHash;

        return vnp_PayUrl + "?" + queryUrl;
    }


    @Transactional
    public Map<String, String> processIPN(Map<String, String> params) {
        int invoiceId = Integer.parseInt(params.get("vnp_TxnRef"));
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy đơn hàng"));
        invoice.setStatus(Status.paid.name());
        invoice.setPurchaseTime(Instant.now());
        invoiceRepository.save(invoice);

        User user = userRepository.findById(invoice.getUser().getId()).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người dùng"));
        if (invoice.getCoins() != null && invoice.getCoins() > 0) {
            user.setCoins(0);
        }

        List<Invoice> invoices = invoiceRepository.findAllByUser_IdAndStatus(user.getId(), Status.paid.name());
        invoices.forEach(i -> {
            sum += i.getTotalPayment();
        });
        if (sum >= 5000000) {
            user.setTier(Tier.silver.name());
        } else if (sum >= 1000000) {
            user.setTier(Tier.gold.name());
        }
        userRepository.save(user);
        sum = 0.0;

        return Map.of("RspCode", "00", "Message", "Confirm Success");
    }
}