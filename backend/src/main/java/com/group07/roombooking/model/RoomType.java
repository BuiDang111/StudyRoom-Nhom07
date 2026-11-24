package com.group07.roombooking.model;

/**
 * Enum định nghĩa 3 loại phòng học nhóm theo đề bài chính thức của Nhóm 07.
 */
public enum RoomType {
    NORMAL("Phòng thường"),
    PROJECTOR("Phòng có máy chiếu"),
    SEMINAR("Phòng họp seminar");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
