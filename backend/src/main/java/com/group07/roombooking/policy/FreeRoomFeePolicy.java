package com.group07.roombooking.policy;

import java.math.BigDecimal;

/**
 * Chính sách tính phí cho Phòng thường (NormalRoom): Miễn phí (0 VND/giờ).
 */
public class FreeRoomFeePolicy implements RoomFeePolicy {

    @Override
    public BigDecimal getHourlyRate() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateFee(long durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Thời lượng đặt phòng phải lớn hơn 0 phút.");
        }
        return BigDecimal.ZERO;
    }
}
