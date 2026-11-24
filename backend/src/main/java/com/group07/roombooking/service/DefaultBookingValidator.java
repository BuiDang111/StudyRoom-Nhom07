package com.group07.roombooking.service;

import com.group07.roombooking.exception.BusinessRuleException;
import com.group07.roombooking.model.Booking;
import com.group07.roombooking.model.Room;
import com.group07.roombooking.model.Student;
import com.group07.roombooking.model.TimeSlot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Lớp thực thi kiểm tra đầy đủ các quy tắc nghiệp vụ bắt buộc khi đặt phòng:
 * 1. Phòng phải đang ở trạng thái ACTIVE (không cho đặt phòng MAINTENANCE).
 * 2. Số người tham gia phải nguyên dương và không vượt sức chứa tối đa của phòng.
 * 3. Phòng không bị trùng lịch với các ca đang BOOKED (newStart < existingEnd && newEnd > existingStart).
 * 4. Tổng thời gian đặt phòng của sinh viên trên TẤT CẢ các phòng trong cùng một ngày không vượt quá 240 phút (4 giờ).
 *    Các lịch đã CANCELLED không bị tính vào giới hạn này.
 */
@Component
public class DefaultBookingValidator implements BookingValidator {

    private final long maxMinutesPerDay;

    public DefaultBookingValidator(@Value("${app.booking.max-minutes-per-day:240}") long maxMinutesPerDay) {
        this.maxMinutesPerDay = maxMinutesPerDay;
    }

    @Override
    public void validateNewBooking(
            Student student,
            Room room,
            TimeSlot requestedSlot,
            int participantCount,
            List<Booking> allExistingBookings
    ) {
        if (requestedSlot.getStartTime().isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessRuleException("PAST_TIME", "Không thể đặt phòng trong quá khứ.", 400);
        }
        if (requestedSlot.getStartTime().toLocalTime().isBefore(java.time.LocalTime.of(7,0))
                || requestedSlot.getEndTime().toLocalTime().isAfter(java.time.LocalTime.of(22,0))) {
            throw new BusinessRuleException("OUTSIDE_HOURS", "Chỉ nhận đặt trong giờ 07:00–22:00.", 400);
        }
        if (requestedSlot.getDurationMinutes() < 30) {
            throw new BusinessRuleException("TOO_SHORT", "Thời gian đặt tối thiểu 30 phút.", 400);
        }
        // Điều kiện 3: Phòng đang hoạt động
        if (!room.isAvailableForBooking()) {
            throw new BusinessRuleException(
                    "ROOM_MAINTENANCE",
                    "Phòng " + room.getId() + " (" + room.getName() + ") đang trong trạng thái bảo trì, không thể đặt lịch.",
                    409
            );
        }

        // Điều kiện 6: Số người tham gia hợp lệ và không vượt sức chứa
        if (participantCount <= 0) {
            throw new BusinessRuleException(
                    "INVALID_PARTICIPANT_COUNT",
                    "Số lượng người tham gia phải là số nguyên dương (lớn hơn 0).",
                    400
            );
        }
        if (participantCount > room.getCapacity()) {
            throw new BusinessRuleException(
                    "CAPACITY_EXCEEDED",
                    "Số người tham gia (" + participantCount + " người) vượt quá sức chứa tối đa của phòng "
                            + room.getId() + " (" + room.getCapacity() + " người).",
                    400
            );
        }

        // Điều kiện 5: Kiểm tra trùng lịch trên cùng phòng (chỉ xét các lịch đang BOOKED)
        for (Booking existing : allExistingBookings) {
            if (!existing.isActiveBooked()) {
                continue;
            }
            if (existing.getRoomId().equalsIgnoreCase(room.getId())) {
                TimeSlot existingSlot = existing.toTimeSlot();
                if (requestedSlot.overlapsWith(existingSlot)) {
                    throw new BusinessRuleException(
                            "ROOM_TIME_CONFLICT",
                            "Phòng " + room.getId() + " đã có lịch đặt (" + existing.getId() + ") từ "
                                    + existing.getStartTime().toLocalTime() + " đến "
                                    + existing.getEndTime().toLocalTime() + ".",
                            409
                    );
                }
            }
        }

        // Điều kiện 7: Giới hạn tối đa 240 phút (4 giờ) mỗi ngày trên tất cả các phòng cho một sinh viên
        LocalDate targetDate = requestedSlot.getDate();
        long bookedMinutesToday = allExistingBookings.stream()
                .filter(Booking::isActiveBooked)
                .filter(b -> b.getStudentId().equalsIgnoreCase(student.getId()))
                .filter(b -> b.getBookingDate().equals(targetDate))
                .mapToLong(Booking::getDurationMinutes)
                .sum();

        long requestedMinutes = requestedSlot.getDurationMinutes();
        if (bookedMinutesToday + requestedMinutes > maxMinutesPerDay) {
            long remainingMinutes = Math.max(0, maxMinutesPerDay - bookedMinutesToday);
            throw new BusinessRuleException(
                    "DAILY_LIMIT_EXCEEDED",
                    "Sinh viên " + student.getId() + " đã đặt " + bookedMinutesToday + " phút trong ngày " + targetDate
                            + ". Yêu cầu thêm " + requestedMinutes + " phút vượt quá giới hạn tối đa "
                            + maxMinutesPerDay + " phút (4 giờ)/ngày (còn lại " + remainingMinutes + " phút).",
                    409
            );
        }
    }
}
