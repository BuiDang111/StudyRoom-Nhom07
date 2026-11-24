package com.group07.roombooking.model;

import com.group07.roombooking.policy.RoomFeePolicy;
import com.group07.roombooking.policy.SeminarRoomFeePolicy;

/**
 * Class SeminarRoom đại diện cho Phòng họp seminar (50.000 VND/giờ).
 * Áp dụng Kế thừa (Inheritance) và Đa hình (Polymorphism).
 */
public class SeminarRoom extends Room {

    public SeminarRoom() {
        super();
    }

    public SeminarRoom(String id, String name, int floor, int capacity, RoomStatus status) {
        super(id, name, floor, capacity, status);
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.SEMINAR;
    }

    @Override
    public RoomFeePolicy createFeePolicy() {
        return new SeminarRoomFeePolicy();
    }
}
