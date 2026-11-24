package com.group07.roombooking.model;

/**
 * Enum định nghĩa trạng thái hoạt động của phòng học nhóm.
 * ACTIVE: Đang hoạt động (cho phép đặt phòng).
 * MAINTENANCE: Đang bảo trì (không cho phép đặt phòng).
 */
public enum RoomStatus {
    ACTIVE("Đang hoạt động"),
    MAINTENANCE("Đang bảo trì");

    private final String vietnameseLabel;

    RoomStatus(String vietnameseLabel) {
        this.vietnameseLabel = vietnameseLabel;
    }

    public String getVietnameseLabel() {
        return vietnameseLabel;
    }
}
