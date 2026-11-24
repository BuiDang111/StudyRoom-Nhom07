package com.group07.roombooking.service;

import com.group07.roombooking.dto.BookingResponseDto;
import com.group07.roombooking.dto.CancelBookingRequest;
import com.group07.roombooking.dto.CreateBookingRequest;
import com.group07.roombooking.exception.BusinessRuleException;
import com.group07.roombooking.exception.ResourceNotFoundException;
import com.group07.roombooking.model.*;
import com.group07.roombooking.repository.BookingRepository;
import com.group07.roombooking.repository.RoomRepository;
import com.group07.roombooking.repository.StudentRepository;
import com.group07.roombooking.utils.IdGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Tầng Service trung tâm xử lý Đặt phòng và Hủy lịch đặt phòng.
 * Đảm bảo:
 * 1. Đồng bộ hóa toàn bộ chu trình Đọc -> Kiểm tra 7 quy tắc -> Tính phí đa hình -> Ghi file JSON
 *    bằng ReentrantLock để không xảy ra tranh chấp đồng thời (race condition) khi 2 request gửi cùng lúc.
 * 2. Áp dụng Polymorphism (Đa hình): Gọi room.calculateBookingFee(durationMinutes) thông qua lớp trừu tượng Room
 *    và interface RoomFeePolicy, hoàn toàn không dùng if/else để phân nhánh cách tính tiền từng loại phòng.
 */
@Service
public class BookingService {

    private static final DateTimeFormatter INPUT_DATE_TIME_FMT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm").withResolverStyle(java.time.format.ResolverStyle.STRICT);

    private final BookingRepository bookingRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final BookingValidator bookingValidator;
    private final ReentrantLock bookingLock = new ReentrantLock(true);

    public BookingService(
            BookingRepository bookingRepository,
            StudentRepository studentRepository,
            RoomRepository roomRepository,
            BookingValidator bookingValidator
    ) {
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.bookingValidator = bookingValidator;
    }

    /**
     * Xử lý tạo lịch đặt phòng mới.
     */
    public BookingResponseDto createBooking(CreateBookingRequest request) {
        if (request == null) {
            throw new BusinessRuleException("INVALID_REQUEST", "Dữ liệu yêu cầu đặt phòng không được để trống.", 400);
        }
        if (request.studentId() == null || request.studentId().trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_STUDENT_ID", "Mã sinh viên không được để trống.", 400);
        }
        if (request.roomId() == null || request.roomId().trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_ROOM_ID", "Mã phòng không được để trống.", 400);
        }
        if (request.participantCount() == null || request.participantCount() <= 0) {
            throw new BusinessRuleException("INVALID_PARTICIPANT_COUNT", "Số lượng người tham gia phải là số nguyên dương.", 400);
        }

        LocalDateTime startTime = parseDateTimeOrThrow(request.startTime(), "Thời gian bắt đầu");
        LocalDateTime endTime = parseDateTimeOrThrow(request.endTime(), "Thời gian kết thúc");

        TimeSlot requestedSlot;
        try {
            requestedSlot = new TimeSlot(startTime, endTime);
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("INVALID_TIME_RANGE", ex.getMessage(), 400);
        }

        bookingLock.lock();
        try {
            // 1. Kiểm tra sinh viên tồn tại
            Student student = studentRepository.findById(request.studentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "STUDENT_NOT_FOUND",
                            "Không tìm thấy sinh viên với mã: " + request.studentId()
                    ));

            // 2. Kiểm tra phòng tồn tại (nhận về kiểu trừu tượng Room)
            Room room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "ROOM_NOT_FOUND",
                            "Không tìm thấy phòng học nhóm với mã: " + request.roomId()
                    ));

            // Đọc danh sách lịch đặt hiện tại từ JSON
            List<Booking> existingBookings = new ArrayList<>(bookingRepository.findAll());

            // 3, 4, 5, 6, 7: Kiểm tra trạng thái phòng, sức chứa, trùng lịch và giới hạn 4 giờ/ngày
            bookingValidator.validateNewBooking(
                    student,
                    room,
                    requestedSlot,
                    request.participantCount(),
                    existingBookings
            );

            // Tạo mã đặt phòng duy nhất (không trùng sau khi restart)
            String newBookingId = IdGenerator.nextBookingId(existingBookings);

            // Tính phí sử dụng phòng thông qua Đa hình (Polymorphism: Room -> RoomFeePolicy)
            long durationMinutes = requestedSlot.getDurationMinutes();
            BigDecimal totalFee = room.calculateBookingFee(durationMinutes);

            Booking newBooking = new Booking(
                    newBookingId,
                    student.getId(),
                    room.getId(),
                    requestedSlot.getStartTime(),
                    requestedSlot.getEndTime(),
                    request.participantCount(),
                    totalFee,
                    BookingStatus.BOOKED,
                    LocalDateTime.now()
            );

            existingBookings.add(newBooking);

            // Lưu xuống file JSON; nếu lưu thất bại sẽ ném DataStorageException và không trả về thành công
            bookingRepository.saveAll(existingBookings);

            return BookingResponseDto.fromModel(
                    newBooking,
                    student,
                    room,
                    "Đặt phòng thành công! Mã đặt phòng của bạn là " + newBookingId + "."
            );
        } finally {
            bookingLock.unlock();
        }
    }

    /**
     * Xử lý hủy lịch đặt phòng.
     * Cập nhật trạng thái thành CANCELLED thay vì xóa khỏi lịch sử.
     */
    public BookingResponseDto cancelBooking(String bookingId, CancelBookingRequest request) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_BOOKING_ID", "Mã đặt phòng không được để trống.", 400);
        }
        if (request == null || request.studentId() == null || request.studentId().trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_STUDENT_ID", "Mã sinh viên yêu cầu hủy không được để trống.", 400);
        }

        bookingLock.lock();
        try {
            List<Booking> allBookings = new ArrayList<>(bookingRepository.findAll());
            Booking targetBooking = allBookings.stream()
                    .filter(b -> b.getId().equalsIgnoreCase(bookingId.trim()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "BOOKING_NOT_FOUND",
                            "Không tìm thấy lịch đặt phòng với mã: " + bookingId
                    ));

            // Gọi phương thức đóng gói Booking.cancel() để kiểm tra đúng chủ sở hữu và chưa bị hủy
            targetBooking.cancel(request.studentId().trim());

            // Ghi lại vào JSON
            bookingRepository.saveAll(allBookings);

            Student student = studentRepository.findById(targetBooking.getStudentId()).orElse(null);
            Room room = roomRepository.findById(targetBooking.getRoomId()).orElse(null);

            return BookingResponseDto.fromModel(
                    targetBooking,
                    student,
                    room,
                    "Đã hủy lịch đặt phòng " + targetBooking.getId() + " thành công."
            );
        } finally {
            bookingLock.unlock();
        }
    }

    /**
     * Xem danh sách lịch đặt của một sinh viên.
     */
    public List<BookingResponseDto> getBookingsByStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "STUDENT_NOT_FOUND",
                        "Không tìm thấy sinh viên với mã: " + studentId
                ));

        Map<String, Room> roomMap = roomRepository.findAll().stream()
                .collect(Collectors.toMap(Room::getId, r -> r, (a, b) -> a));

        return bookingRepository.findByStudentId(student.getId()).stream()
                .map(b -> BookingResponseDto.fromModel(b, student, roomMap.get(b.getRoomId()), null))
                .collect(Collectors.toList());
    }

    /**
     * Xem toàn bộ danh sách lịch đặt trong hệ thống.
     */
    public List<BookingResponseDto> getAllBookings() {
        Map<String, Student> studentMap = studentRepository.findAll().stream()
                .collect(Collectors.toMap(Student::getId, s -> s, (a, b) -> a));
        Map<String, Room> roomMap = roomRepository.findAll().stream()
                .collect(Collectors.toMap(Room::getId, r -> r, (a, b) -> a));

        return bookingRepository.findAll().stream()
                .map(b -> BookingResponseDto.fromModel(
                        b,
                        studentMap.get(b.getStudentId()),
                        roomMap.get(b.getRoomId()),
                        null
                ))
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDateTimeOrThrow(String raw, String fieldLabel) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new BusinessRuleException(
                    "INVALID_TIME_RANGE",
                    fieldLabel + " không được để trống (định dạng chuẩn: YYYY-MM-DDTHH:mm).",
                    400
            );
        }
        try {
            return LocalDateTime.parse(raw.trim(), INPUT_DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessRuleException(
                    "INVALID_TIME_RANGE",
                    fieldLabel + " không đúng định dạng YYYY-MM-DDTHH:mm: " + raw,
                    400
            );
        }
    }
}
