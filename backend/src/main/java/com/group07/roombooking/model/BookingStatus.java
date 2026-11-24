package com.group07.roombooking.model;

/**
 * Enum định nghĩa trạng thái của một lịch đặt phòng (Booking).
 * BOOKED: Đã đặt thành công (đang giữ chỗ và tính vào giới hạn 4 giờ/ngày).
 * CANCELLED: Đã hủy (giữ lại lịch sử nhưng giải phóng phòng và hoàn lại quỹ giờ trong ngày).
 */
public enum BookingStatus {
    BOOKED("Đã đặt"),
    CANCELLED("Đã hủy");

    private final String vietnameseLabel;

    BookingStatus(String vietnameseLabel) {
        this.vietnameseLabel = vietnameseLabel;
    }

    public String getVietnameseLabel() {
        return vietnameseLabel;
    }
}
