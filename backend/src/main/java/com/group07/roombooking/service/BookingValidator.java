package com.group07.roombooking.service;

import com.group07.roombooking.model.Booking;
import com.group07.roombooking.model.Room;
import com.group07.roombooking.model.Student;
import com.group07.roombooking.model.TimeSlot;

import java.util.List;

/**
 * Interface BookingValidator kiểm tra tính hợp lệ của một yêu cầu đặt phòng.
 * Được thiết kế theo đúng gợi ý trong mục 4 của đề bài chính thức Nhóm 07.
 */
public interface BookingValidator {

    void validateNewBooking(
            Student student,
            Room room,
            TimeSlot requestedSlot,
            int participantCount,
            List<Booking> allExistingBookings
    );
}
