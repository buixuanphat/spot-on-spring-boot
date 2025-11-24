package com.buixuanphat.spot_on.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    USER_EXISTED("Email đã được sử dụng.", HttpStatus.CONFLICT.value()),
    USER_NOT_FOUND("Người dùng không tồn tại.", HttpStatus.NOT_FOUND.value()),
    WRONG_PASSWORD("Mật khẩu không chính xác", HttpStatus.UNAUTHORIZED.value()),
    ORGANIZER_NOT_FOUND("Ban tổ chức không tồn tại", HttpStatus.NOT_FOUND.value()),
    FIRSTNAME_INVALID("Tên phải lớn hơn 3 kí tự và bé hơn 20 kí tự", HttpStatus.BAD_REQUEST.value()),
    LASTNAME_INVALID("Họ phải lớn hơn 3 kí tự và bé hơn 20 kí tự", HttpStatus.BAD_REQUEST.value()),
    PASSWORD_INVALID("Mật khẩu phải lớn hơn 8 kí tự và bé hơn 16 kí tự", HttpStatus.BAD_REQUEST.value()),
    EMAIL_INVALID("Địa chỉ email không hợp lệ", HttpStatus.BAD_REQUEST.value()),
    UNAUTHENTICATED("Người dùng chưa được xác thực", HttpStatus.UNAUTHORIZED.value()),
    UNAUTHORIZE("Không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN.value()),
    PHONE_NUMBER_USED("Số điện thoại này đã được sử dụng", HttpStatus.BAD_REQUEST.value()),
    BANK_USED("Tài khoản ngân hàng này đã được sử dụng",  HttpStatus.BAD_REQUEST.value()),
    NAME_USED("Tên này đã được sử dụng",  HttpStatus.BAD_REQUEST.value()),
    TAX_CODE_USED("Mã số thuế đã được sử dụng",  HttpStatus.BAD_REQUEST.value()),
    NAME_INVALID("Tên phải lớn hơn 3 kí tự và bé hơn 100 kí tự", HttpStatus.BAD_REQUEST.value()),
    TAX_CODE_INVALID("Mã số thuế không hợp lệ", HttpStatus.BAD_REQUEST.value()),
    BANK_ACCOUNT_INVALID("Tài khoản ngân hàng không hợp lệ", HttpStatus.BAD_REQUEST.value()),
    PHONE_NUMBER_INVALID("Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST.value()),
    ADDRESS_INVALID("Địa chỉ phải lớn hơn 3 kí tự và bé hơn 100 kí tự", HttpStatus.BAD_REQUEST.value()),
    DESCRIPTION_INVALID("Mô tả phải lớn hơn 3 kí tự và bé hơn 100 kí tự", HttpStatus.BAD_REQUEST.value()),
    AGE_LIMIT_INVALID("Độ tuổi giới hạn nằm trong khoản 0 đến 18", HttpStatus.BAD_REQUEST.value()),
    TICKET_LIMIT_INVALID("Giới hạn số lượng vé cho khu vực phải lớn hơn 0", HttpStatus.BAD_REQUEST.value()),
    EVENT_NOT_FOUND("Không tìm thấy sự kiện", HttpStatus.NOT_FOUND.value()),
    ;

    private final String message;
    private  final int code;

    ErrorMessage(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
