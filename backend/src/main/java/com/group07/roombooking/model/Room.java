package com.group07.roombooking.model;

import com.group07.roombooking.policy.RoomFeePolicy;

import java.math.BigDecimal;

/**
 * Abstract class Room đại diện cho Phòng học nhóm chung trong trường.
 * Áp dụng:
 * - Abstraction (Trừu tượng): Implement interface Bookable và định nghĩa các phương thức abstract
 *   getRoomType(), createFeePolicy() buộc các lớp phòng cụ thể phải cài đặt.
 * - Encapsulation (Đóng gói): Các thuộc tính id, name, floor, capacity, status đều là private.
 * - Polymorphism (Đa hình): Tính phí qua RoomFeePolicy mà không cần dùng if/else kiểm tra loại phòng.
 */
public abstract class Room implements Bookable {
    private String id;
    private String name;
    private int floor;
    private int capacity;
    private RoomStatus status;

    protected Room() {
    }

    protected Room(String id, String name, int floor, int capacity, RoomStatus status) {
        setId(id);
        setName(name);
        setFloor(floor);
        setCapacity(capacity);
        setStatus(status);
    }

    public abstract RoomType getRoomType();

    public abstract RoomFeePolicy createFeePolicy();

    public String getDisplayTypeName() {
        return getRoomType().getDisplayName();
    }

    public BigDecimal getHourlyRate() {
        return createFeePolicy().getHourlyRate();
    }

    @Override
    public BigDecimal calculateBookingFee(long durationMinutes) {
        return createFeePolicy().calculateFee(durationMinutes);
    }

    @Override
    public boolean isAvailableForBooking() {
        return this.status == RoomStatus.ACTIVE;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được để trống.");
        }
        this.id = id.trim();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được để trống.");
        }
        this.name = name.trim();
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        if (floor <= 0) {
            throw new IllegalArgumentException("Tầng của phòng phải là số nguyên dương.");
        }
        this.floor = floor;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa tối đa phải là số nguyên dương.");
        }
        this.capacity = capacity;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái phòng không được để trống.");
        }
        this.status = status;
    }
}
