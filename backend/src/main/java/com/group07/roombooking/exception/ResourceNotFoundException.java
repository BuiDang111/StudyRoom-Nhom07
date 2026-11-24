package com.group07.roombooking.exception;

/**
 * Ngoại lệ trả về khi không tìm thấy Sinh viên, Phòng học hoặc Lịch đặt (HTTP 404).
 */
public class ResourceNotFoundException extends BusinessRuleException {

    public ResourceNotFoundException(String code, String message) {
        super(code, message, 404);
    }
}
