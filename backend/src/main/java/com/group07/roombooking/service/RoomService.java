package com.group07.roombooking.service;

import com.group07.roombooking.dto.RoomAvailabilityDto;
import com.group07.roombooking.dto.RoomResponseDto;
import com.group07.roombooking.exception.BusinessRuleException;
import com.group07.roombooking.exception.ResourceNotFoundException;
import com.group07.roombooking.model.Booking;
import com.group07.roombooking.model.Room;
import com.group07.roombooking.model.Student;
import com.group07.roombooking.repository.BookingRepository;
import com.group07.roombooking.repository.RoomRepository;
import com.group07.roombooking.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tầng Service xử lý nghiệp vụ tìm kiếm phòng, xem chi tiết phòng và tính toán các khoảng giờ trống.
 */
@Service
public class RoomService {

    private static final DateTimeFormatter MINUTE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final StudentRepository studentRepository;

    public RoomService(
            RoomRepository roomRepository,
            BookingRepository bookingRepository,
            StudentRepository studentRepository
    ) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
    }

    public List<RoomResponseDto> searchRooms(String search, String type, String status) {
        return roomRepository.findAll().stream()
                .filter(room -> {
                    if (search != null && !search.trim().isEmpty()) {
                        String q = search.trim().toLowerCase();
                        boolean matchId = room.getId().toLowerCase().contains(q);
                        boolean matchName = room.getName().toLowerCase().contains(q);
                        boolean matchType = room.getRoomType().name().toLowerCase().contains(q)
                                || room.getDisplayTypeName().toLowerCase().contains(q);
                        if (!matchId && !matchName && !matchType) {
                            return false;
                        }
                    }
                    if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("ALL")) {
                        if (!room.getRoomType().name().equalsIgnoreCase(type.trim())) {
                            return false;
                        }
                    }
                    if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
                        if (!room.getStatus().name().equalsIgnoreCase(status.trim())) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(RoomResponseDto::fromModel)
                .collect(Collectors.toList());
    }

    public Room getRoomModelByIdOrThrow(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new ResourceNotFoundException("ROOM_NOT_FOUND", "Mã phòng không được để trống.");
        }
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ROOM_NOT_FOUND",
                        "Không tìm thấy phòng học nhóm với mã: " + roomId
                ));
    }

    public RoomResponseDto getRoomDetail(String roomId) {
        Room room = getRoomModelByIdOrThrow(roomId);
        return RoomResponseDto.fromModel(room);
    }

    public RoomAvailabilityDto getRoomAvailability(String roomId, String dateStr) {
        Room room = getRoomModelByIdOrThrow(roomId);
        LocalDate targetDate;
        try {
            targetDate = (dateStr == null || dateStr.trim().isEmpty())
                    ? LocalDate.now()
                    : LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessRuleException(
                    "INVALID_DATE_FORMAT",
                    "Định dạng ngày không hợp lệ (cần theo chuẩn YYYY-MM-DD): " + dateStr,
                    400
            );
        }

        Map<String, String> studentNames = studentRepository.findAll().stream()
                .collect(Collectors.toMap(Student::getId, Student::getFullName, (a, b) -> a));

        List<Booking> activeBookings = bookingRepository.findByRoomIdAndDate(room.getId(), targetDate).stream()
                .filter(Booking::isActiveBooked)
                .sorted()
                .toList();

        List<RoomAvailabilityDto.BookedSlotDto> bookedSlotDtos = activeBookings.stream()
                .map(b -> new RoomAvailabilityDto.BookedSlotDto(
                        b.getId(),
                        b.getStudentId(),
                        studentNames.getOrDefault(b.getStudentId(), b.getStudentId()),
                        b.getStartTime().format(MINUTE_FMT),
                        b.getEndTime().format(MINUTE_FMT),
                        b.getDurationMinutes(),
                        b.getParticipantCount(),
                        b.getStatus().name()
                ))
                .toList();

        // Tính các khoảng thời gian còn trống trong ngày (00:00 -> 23:59)
        List<RoomAvailabilityDto.AvailableIntervalDto> availableIntervals = new ArrayList<>();
        if (room.isAvailableForBooking()) {
            LocalDateTime cursor = LocalDateTime.of(targetDate, LocalTime.of(7, 0));
            LocalDateTime dayClose = LocalDateTime.of(targetDate, LocalTime.of(22, 0));

            for (Booking booking : activeBookings) {
                LocalDateTime bStart = booking.getStartTime();
                LocalDateTime bEnd = booking.getEndTime();
                if (bStart.isAfter(cursor)) {
                    long gap = Duration.between(cursor, bStart).toMinutes();
                    if (gap > 0) {
                        availableIntervals.add(new RoomAvailabilityDto.AvailableIntervalDto(
                                cursor.format(MINUTE_FMT),
                                bStart.format(MINUTE_FMT),
                                gap
                        ));
                    }
                }
                if (bEnd.isAfter(cursor)) {
                    cursor = bEnd;
                }
            }

            if (cursor.isBefore(dayClose)) {
                long gap = Duration.between(cursor, dayClose).toMinutes();
                if (gap > 0) {
                    availableIntervals.add(new RoomAvailabilityDto.AvailableIntervalDto(
                            cursor.format(MINUTE_FMT),
                            dayClose.format(MINUTE_FMT),
                            gap
                    ));
                }
            }
        }

        return new RoomAvailabilityDto(
                RoomResponseDto.fromModel(room),
                targetDate.toString(),
                room.isAvailableForBooking(),
                bookedSlotDtos,
                availableIntervals
        );
    }
}
