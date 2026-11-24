package com.group07.roombooking.dto;

import com.group07.roombooking.model.Room;

import java.math.BigDecimal;

/**
 * DTO trả về thông tin chi tiết phòng học nhóm cho Frontend.
 */
public record RoomResponseDto(
        String id,
        String name,
        int floor,
        int capacity,
        String type,
        String displayTypeName,
        String status,
        String statusLabel,
        BigDecimal hourlyRate
) {
    public static RoomResponseDto fromModel(Room room) {
        return new RoomResponseDto(
                room.getId(),
                room.getName(),
                room.getFloor(),
                room.getCapacity(),
                room.getRoomType().name(),
                room.getDisplayTypeName(),
                room.getStatus().name(),
                room.getStatus().getVietnameseLabel(),
                room.getHourlyRate()
        );
    }
}
