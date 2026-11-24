package com.group07.roombooking;

import com.group07.roombooking.dto.BookingResponseDto;
import com.group07.roombooking.dto.CancelBookingRequest;
import com.group07.roombooking.dto.CreateBookingRequest;
import com.group07.roombooking.exception.BusinessRuleException;
import com.group07.roombooking.exception.DataStorageException;
import com.group07.roombooking.exception.ResourceNotFoundException;
import com.group07.roombooking.model.*;
import com.group07.roombooking.repository.BookingRepository;
import com.group07.roombooking.repository.RoomRepository;
import com.group07.roombooking.repository.StudentRepository;
import com.group07.roombooking.service.BookingService;
import com.group07.roombooking.service.DefaultBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động JUnit 5 cho Hệ thống Quản lý Đặt phòng Học nhóm (Nhóm 07).
 * Bao phủ đầy đủ 19 kịch bản nghiệp vụ, tính phí đa hình, lưu trữ file JSON và xử lý đồng thời.
 */
class BookingSystemComprehensiveTest {

    @TempDir
    Path tempDataDir;

    private StudentRepository studentRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        studentRepository = new StudentRepository(tempDataDir.toString());
        roomRepository = new RoomRepository(tempDataDir.toString());
        bookingRepository = new BookingRepository(tempDataDir.toString());
        DefaultBookingValidator validator = new DefaultBookingValidator(240L);
        bookingService = new BookingService(bookingRepository, studentRepository, roomRepository, validator);

        // Khởi tạo dữ liệu mẫu cô lập cho mỗi bài test
        List<Student> students = List.of(
                new Student("SV001", "Nguyễn Văn An", "0901234567", "CNTT1-K68", "an.nv@stu.edu.vn"),
                new Student("SV002", "Trần Thị Bình", "0912345678", "CNTT2-K68", "binh.tt@stu.edu.vn")
        );
        studentRepository.saveAll(students);

        List<Room> rooms = List.of(
                new NormalRoom("R101", "Phòng Tự Học A1", 1, 6, RoomStatus.ACTIVE),
                new ProjectorRoom("R201", "Phòng Máy Chiếu B1", 2, 10, RoomStatus.ACTIVE),
                new ProjectorRoom("R202", "Phòng Máy Chiếu B2", 2, 12, RoomStatus.MAINTENANCE),
                new SeminarRoom("R301", "Phòng Seminar C1", 3, 20, RoomStatus.ACTIVE)
        );
        roomRepository.saveAll(rooms);

        bookingRepository.saveAll(new ArrayList<>());
    }

    @Test
    @DisplayName("1. Đặt phòng thành công và lưu trạng thái BOOKED")
    void test01_CreateBookingSuccess() {
        CreateBookingRequest req = new CreateBookingRequest(
                "SV001", "R201", "2099-11-01T08:00", "2099-11-01T09:30", 5
        );
        BookingResponseDto res = bookingService.createBooking(req);

        assertNotNull(res.id());
        assertEquals("BOOKED", res.status());
        assertEquals(90L, res.durationMinutes());
        assertEquals(0, new BigDecimal("30000").compareTo(res.totalFee()));
        assertEquals(1, bookingRepository.findAll().size());
    }

    @Test
    @DisplayName("2. Đặt phòng không tồn tại trả lỗi 404 ROOM_NOT_FOUND")
    void test02_RoomNotFound() {
        CreateBookingRequest req = new CreateBookingRequest(
                "SV001", "R999", "2099-11-01T08:00", "2099-11-01T09:00", 4
        );
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.createBooking(req)
        );
        assertEquals("ROOM_NOT_FOUND", ex.getCode());
        assertEquals(404, ex.getHttpStatus());
    }

    @Test
    @DisplayName("3. Sinh viên không tồn tại trả lỗi 404 STUDENT_NOT_FOUND")
    void test03_StudentNotFound() {
        CreateBookingRequest req = new CreateBookingRequest(
                "SV999", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 4
        );
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.createBooking(req)
        );
        assertEquals("STUDENT_NOT_FOUND", ex.getCode());
        assertEquals(404, ex.getHttpStatus());
    }

    @Test
    @DisplayName("4. Không cho phép đặt phòng đang bảo trì (MAINTENANCE)")
    void test04_RoomInMaintenance() {
        CreateBookingRequest req = new CreateBookingRequest(
                "SV001", "R202", "2099-11-01T08:00", "2099-11-01T09:00", 4
        );
        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(req)
        );
        assertEquals("ROOM_MAINTENANCE", ex.getCode());
        assertEquals(409, ex.getHttpStatus());
    }

    @Test
    @DisplayName("5. Không cho phép đặt phòng bị trùng lịch (newStart < existingEnd && newEnd > existingStart)")
    void test05_RoomTimeConflict() {
        bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T09:00", "2099-11-01T11:00", 4
        ));

        CreateBookingRequest overlappingReq = new CreateBookingRequest(
                "SV002", "R101", "2099-11-01T10:00", "2099-11-01T12:00", 4
        );
        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(overlappingReq)
        );
        assertEquals("ROOM_TIME_CONFLICT", ex.getCode());
        assertEquals(409, ex.getHttpStatus());
    }

    @Test
    @DisplayName("6. Hai lịch liền kề (09:00-10:00 và 10:00-11:00) không trùng nhau và được phép đặt")
    void test06_AdjacentTimeSlotsAllowed() {
        bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T09:00", "2099-11-01T10:00", 4
        ));

        BookingResponseDto second = bookingService.createBooking(new CreateBookingRequest(
                "SV002", "R101", "2099-11-01T10:00", "2099-11-01T11:00", 4
        ));
        assertEquals("BOOKED", second.status());
        assertEquals(2, bookingRepository.findAll().size());
    }

    @Test
    @DisplayName("7. Thời gian không hợp lệ (endTime <= startTime hoặc khác ngày)")
    void test07_InvalidTimeRange() {
        // endTime <= startTime
        CreateBookingRequest reverseReq = new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T10:00", "2099-11-01T09:00", 4
        );
        BusinessRuleException ex1 = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(reverseReq)
        );
        assertEquals("INVALID_TIME_RANGE", ex1.getCode());

        // Khác ngày
        CreateBookingRequest crossDayReq = new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T22:00", "2099-11-01T01:00", 4
        );
        BusinessRuleException ex2 = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(crossDayReq)
        );
        assertEquals("INVALID_TIME_RANGE", ex2.getCode());
    }

    @Test
    @DisplayName("8. Số người bằng sức chứa được chấp nhận; vượt sức chứa bị từ chối")
    void test08_CapacityBoundaryAndExceeded() {
        // R101 có sức chứa = 6 -> Đặt đúng 6 người thành công
        BookingResponseDto exactCap = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 6
        ));
        assertEquals(6, exactCap.participantCount());

        // Đặt 7 người -> Lỗi CAPACITY_EXCEEDED
        CreateBookingRequest overCapReq = new CreateBookingRequest(
                "SV002", "R101", "2099-11-01T09:00", "2099-11-01T10:00", 7
        );
        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(overCapReq)
        );
        assertEquals("CAPACITY_EXCEEDED", ex.getCode());
        assertEquals(400, ex.getHttpStatus());
    }

    @Test
    @DisplayName("9. Sinh viên đặt đúng 4 giờ (240 phút) trong một ngày được chấp nhận")
    void test09_ExactFourHoursPerDayAllowed() {
        BookingResponseDto res = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T12:00", 4
        ));
        assertEquals(240L, res.durationMinutes());
        assertEquals("BOOKED", res.status());
    }

    @Test
    @DisplayName("10. Sinh viên đặt quá 4 giờ (vượt 240 phút) trong một ngày bị từ chối")
    void test10_ExceedFourHoursSingleRoomRejected() {
        bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T11:00", 4
        )); // 180 phút

        // Đặt thêm 90 phút = 270 phút > 240 phút
        CreateBookingRequest req2 = new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T13:00", "2099-11-01T14:30", 4
        );
        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(req2)
        );
        assertEquals("DAILY_LIMIT_EXCEEDED", ex.getCode());
        assertEquals(409, ex.getHttpStatus());
    }

    @Test
    @DisplayName("11. Giới hạn 4 giờ/ngày được cộng dồn trên tất cả các phòng khác nhau")
    void test11_DailyLimitAcrossMultipleRooms() {
        bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T10:30", 4
        )); // 150 phút ở phòng R101

        // Đặt tiếp 120 phút ở phòng R201 cùng ngày -> Tổng 270 phút -> Bị chặn
        CreateBookingRequest reqOtherRoom = new CreateBookingRequest(
                "SV001", "R201", "2099-11-01T14:00", "2099-11-01T16:00", 4
        );
        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(reqOtherRoom)
        );
        assertEquals("DAILY_LIMIT_EXCEEDED", ex.getCode());
    }

    @Test
    @DisplayName("12. Hủy lịch của chính mình: chuyển sang CANCELLED, giải phóng phòng và hoàn lại quỹ giờ")
    void test12_CancelOwnBookingReleasesSlotAndQuota() {
        BookingResponseDto created = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T12:00", 4
        )); // Đã dùng hết 240 phút

        BookingResponseDto cancelled = bookingService.cancelBooking(
                created.id(),
                new CancelBookingRequest("SV001")
        );
        assertEquals("CANCELLED", cancelled.status());

        // Sau khi hủy, SV001 có thể đặt lại cùng khung giờ đó mà không bị báo trùng hay quá 4 giờ
        BookingResponseDto rebooked = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T12:00", 4
        ));
        assertEquals("BOOKED", rebooked.status());
    }

    @Test
    @DisplayName("13. Không cho phép hủy lịch của sinh viên khác (403 BOOKING_OWNERSHIP_MISMATCH)")
    void test13_CancelOtherStudentBookingForbidden() {
        BookingResponseDto created = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 4
        ));

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.cancelBooking(created.id(), new CancelBookingRequest("SV002"))
        );
        assertEquals("BOOKING_OWNERSHIP_MISMATCH", ex.getCode());
        assertEquals(403, ex.getHttpStatus());
    }

    @Test
    @DisplayName("14. Không cho phép hủy một lịch hai lần (409 BOOKING_ALREADY_CANCELLED)")
    void test14_CancelBookingTwiceConflict() {
        BookingResponseDto created = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 4
        ));
        bookingService.cancelBooking(created.id(), new CancelBookingRequest("SV001"));

        BusinessRuleException ex = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.cancelBooking(created.id(), new CancelBookingRequest("SV001"))
        );
        assertEquals("BOOKING_ALREADY_CANCELLED", ex.getCode());
        assertEquals(409, ex.getHttpStatus());
    }

    @Test
    @DisplayName("15. Đa hình tính phí (RoomFeePolicy): Phòng thường 0đ, Máy chiếu 20.000đ/h, Seminar 50.000đ/h")
    void test15_PolymorphicFeeCalculation() {
        // Phòng thường 90 phút -> 0 VND
        BookingResponseDto normalRes = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T09:30", 4
        ));
        assertEquals(0, BigDecimal.ZERO.compareTo(normalRes.totalFee()));

        // Phòng máy chiếu 90 phút (1.5 giờ) -> 30.000 VND
        BookingResponseDto projRes = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R201", "2099-11-01T10:00", "2099-11-01T11:30", 6
        ));
        assertEquals(0, new BigDecimal("30000").compareTo(projRes.totalFee()));

        // Phòng seminar 60 phút (1 giờ) -> 50.000 VND
        BookingResponseDto semRes = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R301", "2099-11-01T14:00", "2099-11-01T15:00", 15
        ));
        assertEquals(0, new BigDecimal("50000").compareTo(semRes.totalFee()));
    }

    @Test
    @DisplayName("16. Dữ liệu vẫn tồn tại và mã Booking không bị trùng lặp sau khi khởi động lại")
    void test16_PersistenceAndUniqueIdAcrossRestart() {
        BookingResponseDto first = bookingService.createBooking(new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 4
        ));
        assertEquals("BK0001", first.id());

        // Mô phỏng khởi động lại Backend bằng cách khởi tạo lại toàn bộ Repository & Service trên cùng thư mục
        BookingRepository restartedBookingRepo = new BookingRepository(tempDataDir.toString());
        StudentRepository restartedStudentRepo = new StudentRepository(tempDataDir.toString());
        RoomRepository restartedRoomRepo = new RoomRepository(tempDataDir.toString());
        BookingService restartedService = new BookingService(
                restartedBookingRepo,
                restartedStudentRepo,
                restartedRoomRepo,
                new DefaultBookingValidator(240L)
        );

        assertEquals(1, restartedBookingRepo.findAll().size());
        BookingResponseDto second = restartedService.createBooking(new CreateBookingRequest(
                "SV002", "R101", "2099-11-01T09:00", "2099-11-01T10:00", 4
        ));
        assertEquals("BK0002", second.id());
        assertEquals(2, restartedBookingRepo.findAll().size());
    }

    @Test
    @DisplayName("17. Hai request đặt phòng đồng thời vào cùng khung giờ: chỉ đúng 1 request thành công")
    void test17_ConcurrentConflictingBookings() throws InterruptedException {
        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        Runnable task1 = () -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                bookingService.createBooking(new CreateBookingRequest(
                        "SV001", "R101", "2099-11-01T09:00", "2099-11-01T11:00", 4
                ));
                successCount.incrementAndGet();
            } catch (BusinessRuleException ex) {
                if ("ROOM_TIME_CONFLICT".equals(ex.getCode())) {
                    conflictCount.incrementAndGet();
                }
            } catch (InterruptedException ignored) {
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable task2 = () -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                bookingService.createBooking(new CreateBookingRequest(
                        "SV002", "R101", "2099-11-01T09:30", "2099-11-01T10:30", 4
                ));
                successCount.incrementAndGet();
            } catch (BusinessRuleException ex) {
                if ("ROOM_TIME_CONFLICT".equals(ex.getCode())) {
                    conflictCount.incrementAndGet();
                }
            } catch (InterruptedException ignored) {
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(task1);
        executor.submit(task2);

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertEquals(1, successCount.get(), "Chỉ được phép có đúng 1 request đặt phòng thành công.");
        assertEquals(1, conflictCount.get(), "Request còn lại phải bị từ chối do ROOM_TIME_CONFLICT.");
    }

    @Test
    @DisplayName("18. File JSON bị hỏng định dạng: Ném lỗi DataStorageException, không âm thầm ghi đè")
    void test18_CorruptedJsonThrowsDataStorageException() throws IOException {
        Path bookingsFile = tempDataDir.resolve("bookings.json");
        Files.writeString(bookingsFile, "{ invalid_json_syntax: [ ", StandardCharsets.UTF_8);

        CreateBookingRequest req = new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 4
        );
        DataStorageException ex = assertThrows(
                DataStorageException.class,
                () -> bookingService.createBooking(req)
        );
        assertEquals("CORRUPTED_JSON_DATA", ex.getCode());
    }

    @Test
    @DisplayName("19. Kiểm tra các trường hợp đầu vào không hợp lệ (số người <= 0, thiếu mã phòng/SV)")
    void test19_InvalidInputsRejected() {
        // Số người = 0
        CreateBookingRequest zeroParticipants = new CreateBookingRequest(
                "SV001", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 0
        );
        BusinessRuleException ex1 = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(zeroParticipants)
        );
        assertEquals("INVALID_PARTICIPANT_COUNT", ex1.getCode());

        // Mã sinh viên rỗng
        CreateBookingRequest emptyStudent = new CreateBookingRequest(
                "   ", "R101", "2099-11-01T08:00", "2099-11-01T09:00", 3
        );
        BusinessRuleException ex2 = assertThrows(
                BusinessRuleException.class,
                () -> bookingService.createBooking(emptyStudent)
        );
        assertEquals("INVALID_STUDENT_ID", ex2.getCode());
    }

    @Test
    void rejectsPastHoursShortAndInvalidCalendar() {
        for (String[] times : List.of(
            new String[]{"2000-01-01T08:00", "2000-01-01T09:00"},
            new String[]{"2099-11-01T06:00", "2099-11-01T07:00"},
            new String[]{"2099-11-01T21:30", "2099-11-01T22:30"},
            new String[]{"2099-11-01T08:00", "2099-11-01T08:15"},
            new String[]{"2099-02-30T08:00", "2099-02-30T09:00"})) {
            assertThrows(BusinessRuleException.class, () -> bookingService.createBooking(
                new CreateBookingRequest("SV001", "R101", times[0], times[1], 4)));
        }
        assertEquals(0, bookingRepository.findAll().size());
    }

    @Test
    void httpEndpointCreatesAndRejectsConflict() throws Exception {
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
            .standaloneSetup(new com.group07.roombooking.handler.BookingHandler(bookingService))
            .setControllerAdvice(new com.group07.roombooking.handler.GlobalExceptionHandler()).build();
        String body = "{\"studentId\":\"SV001\",\"roomId\":\"R101\",\"startTime\":\"2099-11-01T08:00\",\"endTime\":\"2099-11-01T09:00\",\"participantCount\":4}";
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/bookings")
            .contentType("application/json").content(body))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/bookings")
            .contentType("application/json").content(body))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict());
    }
}
