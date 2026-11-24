# Bản cập nhật 5 thành viên

Nếu tải source từ GitHub, chạy BUILD_WINDOWS.bat thành công trước để tạo runtime/studyroom.jar và runtime/data. Các bước cập nhật bên dưới yêu cầu bộ runtime đã có đầy đủ; không áp dụng trực tiếp cho source chưa build.
Đã thêm đúng tên/MSSV; lớp chưa cung cấp nên để “Chưa cập nhật”, số điện thoại/email là dữ liệu mẫu.

## Giữ lịch đang dùng
1. Tắt cửa sổ Java cũ; giải nén bản mới vào một thư mục KHÁC.
2. Trong thư mục mới, chạy CAP_NHAT_BAN_CU.bat.
3. Dán đường dẫn thư mục StudyRoom cũ. Script sao lưu JAR và data; thay JAR và cập nhật sinh viên. Không sửa bookings.json hay rooms.json.
4. Chạy START_WINDOWS.bat trong thư mục CŨ và Ctrl+F5 trên trình duyệt.
Nếu có lịch của SV001… trong bản cũ, sinh viên mẫu tương ứng vẫn xuất hiện để giữ quyền xem/hủy lịch. Không tự coi lịch đó là của thành viên mới.
Script PowerShell chưa được chạy trực tiếp trên Windows trong môi trường kiểm tra.

## Chạy mới, không cần lịch cũ
Chạy START_WINDOWS.bat trong thư mục bản mới. Danh sách sẽ đúng 5 thành viên, lịch ban đầu rỗng.

## Thêm hoặc đổi tên về sau
Dừng Java, sao lưu và sửa runtime/data/students.json, giữ ID nếu chỉ sửa tên. Khởi động lại và Ctrl+F5. Giao diện đọc GET /api/students nên không cần build lại. Không xóa sinh viên còn được lịch đặt tham chiếu.
