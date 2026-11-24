# StudyRoom Nhóm 07

Hệ thống quản lý đặt phòng học nhóm trong trường, phục vụ bài tập lớn Java OOP.

## Chức năng và công nghệ

Tìm phòng, xem lịch trống, đặt/hủy và xem lịch cá nhân. Kiểm tra bảo trì, trùng lịch, sức chứa và hạn mức 240 phút/ngày/sinh viên. Tính phí theo loại phòng, lưu JSON.

Java 17, Spring Boot 3.2.5, Gson, React, Vite, JUnit 5. Danh tính sinh viên được mô phỏng bằng danh sách chọn; chưa có đăng nhập hoặc phân quyền thật. Kiểm tra chủ lịch dựa vào mã sinh viên gửi lên, không phải cơ chế xác thực tài khoản.

## Cấu trúc

| Đường dẫn | Nội dung |
|---|---|
| backend/src/main/java | Model, policy, service, handler, DTO, repository và tiện ích |
| backend/src/test/java | Kiểm thử tự động |
| backend/data | Dữ liệu mẫu khi chạy backend từ thư mục backend |
| backend/src/main/resources/static | Giao diện đã build phục vụ bởi Spring Boot |
| frontend | Mã nguồn và cấu hình giao diện |
| runtime | JAR và dữ liệu chạy được tạo ở máy người dùng, không commit |
| docs | Tài liệu kỹ thuật |

## Chạy trên Windows

### Lần đầu tải source

1. Clone hoặc giải nén toàn bộ bản đã tích hợp. Dừng ứng dụng cũ trước khi build lại.
2. Cài **JDK 17 trở lên** và **Node.js kèm npm** (môi trường kiểm tra dùng Node 24.22.3). Trong CMD kiểm tra `java -version`, `javac -version`, `node --version`, `npm --version`. Không phải mọi phiên bản mới hơn đều đã được kiểm tra.
3. Mở `BUILD_WINDOWS.bat`. Lần đầu cần Internet tải Maven, thư viện Java và npm. Script chạy test trước khi đóng gói.
4. Chỉ khi hiện `Build thanh cong`, mở `START_WINDOWS.bat`.
5. Chờ `Started RoomBookingApplication`, rồi vào **http://localhost:8080**. Giữ cửa sổ Java mở khi dùng.

Build tạo `runtime/studyroom.jar` và sao chép các JSON mẫu chưa có vào `runtime/data`. Những file runtime đã tồn tại được giữ nguyên. Sao lưu dữ liệu trước khi chủ động thay thế.

### Những lần sau

Mở `START_WINDOWS.bat`. Khi JAR và dữ liệu đã có, không cần chạy npm hay tải thư viện lại. Nếu sửa source, phải build lại.

### Kiểm thử và chạy backend riêng

Mở `TEST_WINDOWS.bat`, hoặc CMD trong backend:

```bat
mvnw.cmd test
mvnw.cmd spring-boot:run
```

Lệnh thứ hai khởi chạy backend trực tiếp với `backend/data`. Đây là bộ dữ liệu khác với `runtime/data` khi chạy JAR đóng gói.

## Linux hoặc macOS

Cần JDK 17+, Node/npm, curl và tar nếu Maven chưa được cài.

```sh
sh BUILD_UNIX.sh
sh START_UNIX.sh
```

Kiểm thử riêng từ backend: `sh mvnw test`. Dùng sh nên không cần đổi quyền executable sau khi tải ZIP.

## Quy tắc và dữ liệu

- Mỗi lượt đặt trong cùng ngày, ít nhất 30 phút, 07:00–22:00, không đặt quá khứ.
- Dữ liệu mẫu gồm năm thành viên; điện thoại/email là minh họa, lớp chưa cập nhật.
- Phí mẫu: phòng thường miễn phí, máy chiếu 20.000đ/giờ, seminar 50.000đ/giờ.
- Cổng 8080 đã dùng: dừng bản cũ trước. Không chạy nhiều tiến trình cùng ghi một bộ JSON.
- Build lỗi tải dependency: kiểm tra Internet và lỗi Maven/npm; không dùng JAR cũ để kết luận build mới thành công.
- CAP_NHAT_BAN_CU dùng cho bộ runtime đã build và dữ liệu cũ, không cần khi chạy mới từ source. Chỉ dùng khi có đủ JAR và JSON trong runtime.

## Tài liệu và nộp bài

- [Kiến trúc](docs/KIEN_TRUC.md).
- [Kiểm tra tích hợp](docs/KIEM_TRA_TICH_HOP.md).
- Cần bổ sung báo cáo, sơ đồ lớp, bảng đóng góp thực tế và tài liệu trình bày trước khi nộp.
- Repo Private chỉ xem được khi tài khoản giảng viên có quyền. Nhóm xác nhận cách nộp và quyền xem theo yêu cầu thầy.
