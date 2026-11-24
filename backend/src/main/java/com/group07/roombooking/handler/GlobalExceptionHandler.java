package com.group07.roombooking.handler;

import com.group07.roombooking.dto.ErrorResponseDto;
import com.group07.roombooking.exception.BusinessRuleException;
import com.group07.roombooking.exception.DataStorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tầng Handler bắt toàn bộ ngoại lệ từ Service / Repository và trả về cấu trúc JSON thống nhất:
 * {
 *   "code": "...",
 *   "message": "..."
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessRuleException(BusinessRuleException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ErrorResponseDto(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("INVALID_INPUT", ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleMalformedRequestBody(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("MALFORMED_JSON_REQUEST", "Dữ liệu JSON gửi lên không đúng định dạng."));
    }

    @ExceptionHandler(DataStorageException.class)
    public ResponseEntity<ErrorResponseDto> handleDataStorageException(DataStorageException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("INTERNAL_SERVER_ERROR", "Đã xảy ra lỗi hệ thống: " + ex.getMessage()));
    }
}
