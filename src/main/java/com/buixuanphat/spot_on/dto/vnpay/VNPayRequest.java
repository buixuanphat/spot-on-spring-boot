package com.buixuanphat.spot_on.dto.vnpay;

import lombok.Data;

@Data
public class VNPayRequest {
    private int amount;
    private String orderInfo;
    private String orderType;
    private String bankCode;
    private String language;

    // billing
    private String billingMobile;
    private String billingEmail;
    private String billingFullname;
    private String billingAddress;
    private String billingCity;
    private String billingCountry;
    private String billingState;

    // invoice
    private String invMobile;
    private String invEmail;
    private String invCustomer;
    private String invAddress;
    private String invCompany;
    private String invTaxcode;
    private String invType;
    private Integer txnRef;
}