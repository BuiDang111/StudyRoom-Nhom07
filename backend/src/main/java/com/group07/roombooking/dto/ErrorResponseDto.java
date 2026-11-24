package com.group07.roombooking.dto;

/**
 * Cấu trúc JSON lỗi thống nhất giữa Backend và Frontend theo thiết kế v1:
 * {
 *   "code": "ROOM_TIME_CONFLICT",
 *   "message": "Phòng đã có lịch trong khoảng thời gian đã chọn."
 * }
 */
public record ErrorResponseDto(
        String code,
        String message
) {
}
