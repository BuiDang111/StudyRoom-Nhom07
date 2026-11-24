package com.group07.roombooking.exception;

/**
 * Ngoại lệ khi xảy ra lỗi đọc/ghi file JSON hoặc file JSON bị hỏng định dạng (HTTP 500).
 * Tuân thủ quy tắc: Không được coi JSON bị hỏng là danh sách rỗng rồi âm thầm ghi đè.
 */
public class DataStorageException extends RuntimeException {
    private final String code;

    public DataStorageException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public DataStorageException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
