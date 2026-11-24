package com.group07.roombooking.handler;

import com.group07.roombooking.dto.BookingResponseDto;
import com.group07.roombooking.dto.CancelBookingRequest;
import com.group07.roombooking.dto.CreateBookingRequest;
import com.group07.roombooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handler tiếp nhận các HTTP request liên quan đến Đặt phòng và Hủy lịch:
 * - GET /api/bookings: Xem toàn bộ lịch đặt
 * - POST /api/bookings: Tạo lịch đặt phòng mới (HTTP 201 Created)
 * - PATCH /api/bookings/{id}/cancel: Hủy lịch đặt phòng (HTTP 200 OK)
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingHandler {

    private final BookingService bookingService;

    public BookingHandler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody CreateBookingRequest request) {
        BookingResponseDto created = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable("id") String bookingId,
            @RequestBody CancelBookingRequest request
    ) {
        BookingResponseDto cancelled = bookingService.cancelBooking(bookingId, request);
        return ResponseEntity.ok(cancelled);
    }
}
