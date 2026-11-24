package com.group07.roombooking.handler;

import com.group07.roombooking.dto.RoomAvailabilityDto;
import com.group07.roombooking.dto.RoomResponseDto;
import com.group07.roombooking.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handler tiếp nhận các HTTP request liên quan đến Phòng học nhóm:
 * - GET /api/rooms: Danh sách và lọc phòng theo search, type, status
 * - GET /api/rooms/{id}: Chi tiết phòng
 * - GET /api/rooms/{id}/availability?date=YYYY-MM-DD: Xem lịch đã đặt và các khoảng giờ còn trống
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomHandler {

    private final RoomService roomService;

    public RoomHandler(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getRooms(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status
    ) {
        return ResponseEntity.ok(roomService.searchRooms(search, type, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable("id") String roomId) {
        return ResponseEntity.ok(roomService.getRoomDetail(roomId));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<RoomAvailabilityDto> getRoomAvailability(
            @PathVariable("id") String roomId,
            @RequestParam(value = "date", required = false) String date
    ) {
        return ResponseEntity.ok(roomService.getRoomAvailability(roomId, date));
    }
}
