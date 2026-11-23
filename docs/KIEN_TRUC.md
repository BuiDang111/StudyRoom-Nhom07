# Kiến trúc hệ thống

StudyRoom gồm giao diện React, backend Java Spring Boot và dữ liệu JSON. Giao diện gửi HTTP request đến backend, nhận dữ liệu hoặc thông báo lỗi để cập nhật màn hình.

## Backend

| Package | Trách nhiệm |
|---|---|
| model | Student, Room, Booking, TimeSlot và các kiểu dữ liệu liên quan |
| policy | Hợp đồng và các chính sách tính phí phòng |
| service | Xử lý nghiệp vụ đặt, hủy, tra cứu và kiểm tra điều kiện |
| handler | Tiếp nhận API và trả phản hồi HTTP |
| dto | Cấu trúc dữ liệu yêu cầu và phản hồi |
| repository | Truy cập dữ liệu sinh viên, phòng và lịch đặt |
| utils | Tiện ích xử lý JSON và sinh mã |
| exception | Các loại lỗi nghiệp vụ và lưu trữ |
| config | Cấu hình ứng dụng |

## Luồng đặt phòng

Handler tiếp nhận yêu cầu và gọi BookingService. Service tìm sinh viên và phòng, kiểm tra các điều kiện qua BookingValidator, tính phí thông qua Room và RoomFeePolicy, sau đó gọi Repository để lưu lịch. Khi một điều kiện không đạt, ứng dụng trả lỗi có nguyên nhân cụ thể.

## Thiết kế hướng đối tượng

- User là lớp cha của Student.
- Room là lớp cha của NormalRoom, ProjectorRoom và SeminarRoom.
- Booking kiểm soát việc thay đổi trạng thái thông qua hành vi hủy.
- RoomFeePolicy mô tả chính sách tính phí; các loại phòng sử dụng chính sách phù hợp.

## Lưu trữ

Dữ liệu gồm students.json, rooms.json và bookings.json. Lịch hủy được giữ lại với trạng thái CANCELLED. JSON phù hợp phạm vi học tập và quy mô nhỏ.

## Phạm vi phiên bản

Danh tính sinh viên được mô phỏng bằng lựa chọn trên giao diện. Đăng nhập, phân quyền và cơ sở dữ liệu quan hệ là các hướng phát triển.
