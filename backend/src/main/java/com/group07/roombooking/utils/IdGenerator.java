package com.group07.roombooking.utils;

import com.group07.roombooking.model.Booking;

import java.util.List;

/**
 * Tiện ích sinh mã đặt phòng tự động (ví dụ: BK0001, BK0002...)
 * Đảm bảo tính duy nhất ngay cả sau khi tắt và khởi động lại Backend bằng cách
 * quét số thứ tự lớn nhất hiện có trong danh sách lịch đặt từ file bookings.json.
 */
public final class IdGenerator {

    private static final String BOOKING_PREFIX = "BK";

    private IdGenerator() {
    }

    public static String nextBookingId(List<Booking> existingBookings) {
        int maxNumber = 0;
        if (existingBookings != null) {
            for (Booking booking : existingBookings) {
                String id = booking.getId();
                if (id != null && id.startsWith(BOOKING_PREFIX)) {
                    try {
                        int parsed = Integer.parseInt(id.substring(BOOKING_PREFIX.length()));
                        if (parsed > maxNumber) {
                            maxNumber = parsed;
                        }
                    } catch (NumberFormatException ignored) {
                        // Bỏ qua các mã tùy chỉnh không theo định dạng BKxxxx
                    }
                }
            }
        }
        return String.format("%s%04d", BOOKING_PREFIX, maxNumber + 1);
    }
}
