package com.group07.roombooking.dto;

/**
 * DTO nhận dữ liệu từ Frontend khi gửi request POST /api/bookings.
 * Khớp chính xác với định dạng JSON trong tài liệu thiết kế v1.
 */
public record CreateBookingRequest(
        String studentId,
        String roomId,
        String startTime,
        String endTime,
        Integer participantCount
) {
}
