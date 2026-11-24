package com.group07.roombooking.repository;

import com.google.gson.reflect.TypeToken;
import com.group07.roombooking.model.Booking;
import com.group07.roombooking.utils.JsonFileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tầng Repository phụ trách đọc/ghi dữ liệu Lịch đặt phòng từ file backend/data/bookings.json.
 */
@Repository
public class BookingRepository {

    private static final Type BOOKING_LIST_TYPE = new TypeToken<List<Booking>>() {}.getType();
    private final Path filePath;

    public BookingRepository(@Value("${app.data.dir:data}") String dataDir) {
        this.filePath = Paths.get(dataDir, "bookings.json");
    }

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>(JsonFileUtils.readListFromFile(filePath, BOOKING_LIST_TYPE));
        try {
            for (Booking booking : bookings) {
                new Booking(booking.getId(), booking.getStudentId(), booking.getRoomId(),
                    booking.getStartTime(), booking.getEndTime(), booking.getParticipantCount(),
                    booking.getTotalFee(), booking.getStatus(), booking.getCreatedAt());
                if (booking.getStatus() == null) throw new IllegalArgumentException("Thiếu trạng thái lịch");
            }
            Collections.sort(bookings);
        } catch (RuntimeException ex) {
            throw new com.group07.roombooking.exception.DataStorageException(
                "INVALID_BOOKING_DATA", "Dữ liệu lịch đặt không hợp lệ. Kiểm tra bookings.json.", ex);
        }
        return bookings;
    }

    public Optional<Booking> findById(String bookingId) {
        if (bookingId == null) {
            return Optional.empty();
        }
        return findAll().stream()
                .filter(b -> b.getId().equalsIgnoreCase(bookingId.trim()))
                .findFirst();
    }

    public List<Booking> findByRoomIdAndDate(String roomId, LocalDate date) {
        return findAll().stream()
                .filter(b -> b.getRoomId().equalsIgnoreCase(roomId.trim()))
                .filter(b -> b.getBookingDate().equals(date))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Booking> findByStudentId(String studentId) {
        return findAll().stream()
                .filter(b -> b.getStudentId().equalsIgnoreCase(studentId.trim()))
                .sorted()
                .collect(Collectors.toList());
    }

    public void saveAll(List<Booking> bookings) {
        JsonFileUtils.writeListToFileAtomically(filePath, bookings, BOOKING_LIST_TYPE);
    }
}
