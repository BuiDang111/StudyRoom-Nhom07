package com.group07.roombooking.policy;

import java.math.BigDecimal;

/**
 * Interface RoomFeePolicy định nghĩa chính sách tính phí sử dụng phòng.
 * Áp dụng:
 * - Abstraction (Trừu tượng) & Polymorphism (Đa hình).
 * - Nguyên lý Open/Closed (OCP): Nếu sau này nhà trường thêm loại phòng mới (ví dụ: Phòng Lab),
 *   chỉ cần tạo class mới implement RoomFeePolicy mà không phải sửa câu lệnh if/else trong BookingService.
 *
 * Lưu ý cấu hình: Các mức phí (0đ, 20.000đ/giờ, 50.000đ/giờ) là giả định cấu hình mẫu theo ví dụ trong đề bài.
 */
public interface RoomFeePolicy {

    /**
     * Trả về đơn giá theo giờ (VND/giờ).
     */
    BigDecimal getHourlyRate();

    /**
     * Tính tổng chi phí sử dụng dựa trên thời lượng đặt phòng (tính bằng phút).
     * Sử dụng BigDecimal để tránh sai số dấu phẩy động khi tính tiền.
     */
    BigDecimal calculateFee(long durationMinutes);
}
