package com.group07.roombooking.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Chính sách tính phí cho Phòng có máy chiếu (ProjectorRoom): 20.000 VND/giờ (mức phí mẫu theo đề bài).
 */
public class ProjectorRoomFeePolicy implements RoomFeePolicy {

    private static final BigDecimal HOURLY_RATE = new BigDecimal("20000");
    private static final BigDecimal MINUTES_PER_HOUR = new BigDecimal("60");

    @Override
    public BigDecimal getHourlyRate() {
        return HOURLY_RATE;
    }

    @Override
    public BigDecimal calculateFee(long durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Thời lượng đặt phòng phải lớn hơn 0 phút.");
        }
        return HOURLY_RATE
                .multiply(BigDecimal.valueOf(durationMinutes))
                .divide(MINUTES_PER_HOUR, 0, RoundingMode.HALF_UP);
    }
}
