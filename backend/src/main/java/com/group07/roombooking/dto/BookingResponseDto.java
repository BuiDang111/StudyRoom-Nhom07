package com.group07.roombooking.dto;

import com.group07.roombooking.model.Booking;
import com.group07.roombooking.model.Room;
import com.group07.roombooking.model.Student;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * DTO trả về thông tin lịch đặt phòng kèm tên sinh viên, thông tin phòng, thời lượng và chi phí.
 */
public record BookingResponseDto(
        String id,
        String studentId,
        String studentName,
        String roomId,
        String roomName,
        String roomType,
        String startTime,
        String endTime,
        long durationMinutes,
        int participantCount,
        BigDecimal totalFee,
        String status,
        String statusLabel,
        String createdAt,
        String message
) {
    private static final DateTimeFormatter MINUTE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static BookingResponseDto fromModel(Booking booking, Student student, Room room, String message) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStudentId(),
                student != null ? student.getFullName() : booking.getStudentId(),
                booking.getRoomId(),
                room != null ? room.getName() : booking.getRoomId(),
                room != null ? room.getRoomType().name() : "UNKNOWN",
                booking.getStartTime().format(MINUTE_FMT),
                booking.getEndTime().format(MINUTE_FMT),
                booking.getDurationMinutes(),
                booking.getParticipantCount(),
                booking.getTotalFee(),
                booking.getStatus().name(),
                booking.getStatus().getVietnameseLabel(),
                booking.getCreatedAt() != null ? booking.getCreatedAt().format(MINUTE_FMT) : "",
                message
        );
    }
}
