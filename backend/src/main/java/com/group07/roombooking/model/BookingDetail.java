package com.group07.roombooking.model;

import java.math.BigDecimal;

/**
 * Class BookingDetail đóng gói thông tin chi tiết về thời lượng, đơn giá và số người của một ca đặt phòng.
 * Được gợi ý trong mục 4 của đề bài chính thức Nhóm 07.
 */
public class BookingDetail {
    private String bookingId;
    private TimeSlot timeSlot;
    private int participantCount;
    private BigDecimal hourlyRate;
    private BigDecimal totalFee;

    public BookingDetail(String bookingId, TimeSlot timeSlot, int participantCount, BigDecimal hourlyRate, BigDecimal totalFee) {
        if (participantCount <= 0) {
            throw new IllegalArgumentException("Số lượng người tham gia phải là số nguyên dương.");
        }
        this.bookingId = bookingId;
        this.timeSlot = timeSlot;
        this.participantCount = participantCount;
        this.hourlyRate = hourlyRate;
        this.totalFee = totalFee;
    }

    public String getBookingId() {
        return bookingId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public long getDurationMinutes() {
        return timeSlot != null ? timeSlot.getDurationMinutes() : 0;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }
}
