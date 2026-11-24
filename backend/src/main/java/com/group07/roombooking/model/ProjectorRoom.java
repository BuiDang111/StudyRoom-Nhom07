package com.group07.roombooking.model;

import com.group07.roombooking.policy.ProjectorRoomFeePolicy;
import com.group07.roombooking.policy.RoomFeePolicy;

/**
 * Class ProjectorRoom đại diện cho Phòng có máy chiếu (20.000 VND/giờ).
 * Áp dụng Kế thừa (Inheritance) và Đa hình (Polymorphism).
 */
public class ProjectorRoom extends Room {

    public ProjectorRoom() {
        super();
    }

    public ProjectorRoom(String id, String name, int floor, int capacity, RoomStatus status) {
        super(id, name, floor, capacity, status);
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.PROJECTOR;
    }

    @Override
    public RoomFeePolicy createFeePolicy() {
        return new ProjectorRoomFeePolicy();
    }
}
