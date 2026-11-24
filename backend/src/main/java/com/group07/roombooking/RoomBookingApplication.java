package com.group07.roombooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Class khởi chạy ứng dụng Spring Boot cho Hệ thống Quản lý Đặt phòng Học nhóm (Nhóm 07).
 * Tuân thủ nguyên tắc: Main chỉ khởi chạy chương trình, tuyệt đối không viết logic nghiệp vụ tại đây.
 */
@SpringBootApplication
public class RoomBookingApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(RoomBookingApplication.class, args);
    }
}
