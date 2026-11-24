package com.group07.roombooking.model;

import com.group07.roombooking.policy.FreeRoomFeePolicy;
import com.group07.roombooking.policy.RoomFeePolicy;

/**
 * Class NormalRoom đại diện cho Phòng thường (Miễn phí sử dụng).
 * Áp dụng Kế thừa (Inheritance) và Đa hình (Polymorphism).
 */
public class NormalRoom extends Room {

    public NormalRoom() {
        super();
    }

    public NormalRoom(String id, String name, int floor, int capacity, RoomStatus status) {
        super(id, name, floor, capacity, status);
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.NORMAL;
    }

    @Override
    public RoomFeePolicy createFeePolicy() {
        return new FreeRoomFeePolicy();
    }
}
