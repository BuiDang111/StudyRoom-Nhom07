package com.group07.roombooking.model;

import java.math.BigDecimal;

/**
 * Interface Bookable mô tả hành vi của một thực thể có thể được đặt lịch theo giờ.
 * Áp dụng nguyên lý Trừu tượng (Abstraction) theo gợi ý trong đề bài mục 4.
 */
public interface Bookable {

    String getId();

    String getName();

    int getCapacity();

    boolean isAvailableForBooking();

    BigDecimal calculateBookingFee(long durationMinutes);
}
