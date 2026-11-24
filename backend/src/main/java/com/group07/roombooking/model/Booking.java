package com.group07.roombooking.model;

import com.group07.roombooking.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class Booking đại diện cho một Lịch đặt phòng học nhóm.
 * Áp dụng:
 * - Encapsulation (Đóng gói) chặt chẽ:
 *   + Toàn bộ thuộc tính là private.
 *   + KHÔNG cung cấp public setter tùy tiện cho thuộc tính nhạy cảm `status`.
 *   + Việc hủy lịch bắt buộc phải đi qua phương thức nghiệp vụ `cancel(String requestingStudentId)`
 *     để kiểm tra quyền sở hữu và trạng thái hiện tại trước khi chuyển sang CANCELLED.
 * - Comparable<Booking>: Sắp xếp lịch đặt theo thời gian bắt đầu.
 */
public class Booking implements Comparable<Booking> {
    private String id;
    private String studentId;
    private String roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int participantCount;
    private BigDecimal totalFee;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public Booking() {
    }

    public Booking(
            String id,
            String studentId,
            String roomId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int participantCount,
            BigDecimal totalFee,
            BookingStatus status,
            LocalDateTime createdAt
    ) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đặt phòng không được để trống.");
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống.");
        }
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được để trống.");
        }
        TimeSlot validatedSlot = new TimeSlot(startTime, endTime);
        if (participantCount <= 0) {
            throw new IllegalArgumentException("Số người tham gia phải là số nguyên dương.");
        }
        if (totalFee == null || totalFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Phí đặt phòng không hợp lệ.");
        }

        this.id = id.trim();
        this.studentId = studentId.trim();
        this.roomId = roomId.trim();
        this.startTime = validatedSlot.getStartTime();
        this.endTime = validatedSlot.getEndTime();
        this.participantCount = participantCount;
        this.totalFee = totalFee;
        this.status = status != null ? status : BookingStatus.BOOKED;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /**
     * Phương thức đóng gói hành vi hủy lịch đặt phòng.
     * Kiểm tra:
     * 1. Sinh viên yêu cầu hủy có đúng là chủ lịch đặt không.
     * 2. Lịch đặt đã bị hủy trước đó chưa.
     */
    public void cancel(String requestingStudentId) {
        if (requestingStudentId == null || !this.studentId.equals(requestingStudentId.trim())) {
            throw new BusinessRuleException(
                    "BOOKING_OWNERSHIP_MISMATCH",
                    "Bạn không có quyền hủy lịch đặt phòng của sinh viên khác.",
                    403
            );
        }
        if (this.status == BookingStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "BOOKING_ALREADY_CANCELLED",
                    "Lịch đặt phòng này đã được hủy trước đó.",
                    409
            );
        }
        this.status = BookingStatus.CANCELLED;
    }

    public boolean isActiveBooked() {
        return this.status == BookingStatus.BOOKED;
    }

    public TimeSlot toTimeSlot() {
        return new TimeSlot(this.startTime, this.endTime);
    }

    public long getDurationMinutes() {
        return Duration.between(this.startTime, this.endTime).toMinutes();
    }

    public LocalDate getBookingDate() {
        return this.startTime.toLocalDate();
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getRoomId() {
        return roomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public int compareTo(Booking other) {
        return this.startTime.compareTo(other.startTime);
    }
}
