package com.group07.roombooking.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class TimeSlot đại diện cho một khoảng thời gian đặt phòng (bắt đầu -> kết thúc).
 * Áp dụng:
 * - Encapsulation: Đảm bảo startTime < endTime và nằm trong cùng một ngày theo thiết kế v1.
 * - Comparable<TimeSlot>: Cho phép sắp xếp các khung giờ theo thứ tự thời gian tăng dần.
 */
public class TimeSlot implements Comparable<TimeSlot> {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu và kết thúc không được để trống.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Thời gian kết thúc phải lớn hơn thời gian bắt đầu.");
        }
        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            throw new IllegalArgumentException("Trong phiên bản v1, một lịch đặt phòng chỉ được nằm trong cùng một ngày.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Kiểm tra hai khoảng thời gian có giao nhau (trùng lịch) hay không.
     * Quy tắc chuẩn thiết kế v1: newStart < existingEnd && newEnd > existingStart.
     * Nhờ đó hai ca liền kề 09:00-10:00 và 10:00-11:00 KHÔNG bị tính là trùng nhau.
     */
    public boolean overlapsWith(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }

    public long getDurationMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    public LocalDate getDate() {
        return startTime.toLocalDate();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public int compareTo(TimeSlot other) {
        int startCmp = this.startTime.compareTo(other.startTime);
        if (startCmp != 0) {
            return startCmp;
        }
        return this.endTime.compareTo(other.endTime);
    }
}
