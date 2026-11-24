package com.group07.roombooking.exception;

/**
 * Ngoại lệ nghiệp vụ chuẩn của hệ thống.
 * Mang mã lỗi (code), thông điệp tiếng Việt rõ ràng (message) và mã HTTP tương ứng (httpStatus).
 */
public class BusinessRuleException extends RuntimeException {
    private final String code;
    private final int httpStatus;

    public BusinessRuleException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
