package com.group07.roombooking.dto;

/**
 * DTO nhận dữ liệu khi gửi request PATCH /api/bookings/{id}/cancel.
 * Ví dụ: { "studentId": "SV001" }
 */
public record CancelBookingRequest(
        String studentId
) {
}
