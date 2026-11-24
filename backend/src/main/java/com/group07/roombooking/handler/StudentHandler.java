package com.group07.roombooking.handler;

import com.group07.roombooking.dto.BookingResponseDto;
import com.group07.roombooking.model.Student;
import com.group07.roombooking.service.BookingService;
import com.group07.roombooking.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handler tiếp nhận các HTTP request liên quan đến Sinh viên:
 * - GET /api/students: Danh sách sinh viên mẫu
 * - GET /api/students/{id}/bookings: Xem lịch đặt của sinh viên
 */
@RestController
@RequestMapping("/api/students")
public class StudentHandler {

    private final StudentService studentService;
    private final BookingService bookingService;

    public StudentHandler(StudentService studentService, BookingService bookingService) {
        this.studentService = studentService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponseDto>> getStudentBookings(@PathVariable("id") String studentId) {
        return ResponseEntity.ok(bookingService.getBookingsByStudent(studentId));
    }
}
