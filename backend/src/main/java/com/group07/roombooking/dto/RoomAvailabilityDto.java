package com.group07.roombooking.dto;

import java.util.List;

/**
 * DTO trả về lịch trống và các ca đã đặt của một phòng trong một ngày cụ thể
 * cho endpoint GET /api/rooms/{id}/availability?date=YYYY-MM-DD.
 */
public record RoomAvailabilityDto(
        RoomResponseDto room,
        String date,
        boolean bookable,
        List<BookedSlotDto> bookedSlots,
        List<AvailableIntervalDto> availableIntervals
) {
    public record BookedSlotDto(
            String bookingId,
            String studentId,
            String studentName,
            String startTime,
            String endTime,
            long durationMinutes,
            int participantCount,
            String status
    ) {
    }

    public record AvailableIntervalDto(
            String startTime,
            String endTime,
            long durationMinutes
    ) {
    }
}
