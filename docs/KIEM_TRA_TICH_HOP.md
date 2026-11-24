# Kiểm tra tích hợp StudyRoom

Ngày kiểm tra: 25/11/2026 (giờ Việt Nam). Mã nguồn gốc đã ghép: commit `fe73d7ea54ffde0ebc1c4bd1f506a4c36304fea8` trên main. Các sửa đổi về script và README được đề xuất trên nhánh riêng; kết quả dưới đây không có nghĩa chúng đã được merge vào main.

## Phạm vi và kết quả

| Kiểm tra | Kết quả |
|---|---|
| Đối chiếu 71 file ứng dụng với ZIP nguồn nhóm chốt | Trùng nội dung qua Git blob SHA, không thiếu file |
| Cấu trúc source | 42 Java chính, 1 file Java test, 4 source frontend |
| npm ci và npm run build | Đạt |
| JUnit | 21 test, 0 thất bại, 0 lỗi, 0 bỏ qua |
| Maven package và Spring Boot repackage | Đạt, tạo JAR thực thi |
| Build bằng BUILD_UNIX.sh đã sửa | Đạt trong môi trường kiểm tra có cấu hình compiler bổ sung bên dưới |
| Khởi động bằng START_UNIX.sh đã sửa | Đạt, chạy JAR và phục vụ HTTP |
| Build lại với runtime/data đã có | Giữ nguyên byte của cả ba file JSON |

## Kiểm tra HTTP trên JAR mới build

Dùng cổng 18080, bộ dữ liệu runtime cục bộ và ngày đặt 01/11/2099; không sửa dữ liệu trên máy các thành viên.

- Trang HTML, JavaScript, CSS và ảnh trả HTTP 200.
- API sinh viên trả đủ 5 người, API phòng trả danh sách phòng.
- Đặt R201 trong 90 phút trả HTTP 201, BOOKED và phí 30.000đ.
- Đặt trùng của sinh viên khác trả HTTP 409.
- Hủy bằng mã sinh viên không phải chủ lịch trả HTTP 403. Đây là kiểm tra mã chủ lịch, không phải xác thực đăng nhập.
- Đặt vượt hạn mức, vượt sức chứa hoặc phòng bảo trì đều bị từ chối bằng lỗi 4xx.
- Chủ lịch hủy thành công; hủy hai lần trả HTTP 409.
- Lịch hủy không giữ chỗ: đặt lại khung giờ đã hủy thành công.
- API lịch trống và lịch cá nhân hoạt động; lịch đã hủy còn trong lịch sử và JSON.
- Dừng hoàn toàn tiến trình Java rồi khởi động lại: vẫn đọc được hai lịch đã hủy.

Có 19 điểm kiểm tra HTTP/nội dung/lưu file, cộng kiểm tra giữ dữ liệu sau khi khởi động lại. Đây là kiểm tra bằng HTTP, không phải kiểm thử thao tác chuột trên trình duyệt.

## Sửa lỗi build và khởi chạy

Bản script gốc sao chép JAR vào runtime nhưng chưa tạo runtime; thiếu bước chuẩn bị dữ liệu. START_WINDOWS.bat cũng chưa phát hiện JAR bị thiếu. Bản sửa:

- Tạo runtime/data và chép JAR sau khi Maven package thành công.
- Chỉ sao chép từng JSON mẫu khi file runtime tương ứng chưa tồn tại.
- Báo thiếu bản build hoặc dữ liệu trước khi khởi chạy.
- BUILD_WINDOWS kiểm tra JDK/npm và lỗi copy; TEST_WINDOWS giữ mã thoát test.
- Thêm BUILD_UNIX.sh cho Linux/macOS, giữ cách chạy bằng sh.
- Công cụ cập nhật bản cũ kiểm tra runtime nguồn trước khi thực hiện sao lưu/thay JAR.
- README phân biệt chạy source backend/data với JAR runtime/data, nêu rõ lần đầu cần build và Internet.

## Môi trường và giới hạn xác nhận

Linux, Java runtime 21.0.13.1, Maven 3.9.6, Node 24.22.3, npm 11.19.1. Môi trường có module compiler nhưng không có bộ JDK/ct.sym đầy đủ. Lệnh release 17 mặc định không thực hiện được ở môi trường này, nên kiểm tra backend dùng tùy chọn source/target 17 thay cho release 17:

```sh
sh mvnw -Dmaven.compiler.release= -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 test
sh mvnw -Dmaven.compiler.release= -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 package
```

Có thêm cấu hình proxy tạm của môi trường để Maven tải dependency. Kiểm tra BUILD_UNIX dùng MAVEN_ARGS tương ứng và một launcher javac cục bộ gọi module compiler của Java; những thiết lập môi trường này không được đưa vào repo. File class đã kiểm tra có major version 61 (Java 17), nhưng điều này không thay thế chạy lại trên JDK 17 thật. pom.xml vẫn giữ cấu hình Java 17 ban đầu.

Các script BAT và PowerShell mới chỉ được rà soát nội dung, chưa chạy trực tiếp trên Windows. Đường build mặc định trên một máy JDK đầy đủ vẫn cần nhóm xác nhận. Chưa kiểm thử giao diện bằng trình duyệt tự động ở lượt này. Không kết luận hệ thống không còn lỗi hoặc sẵn sàng production.

## Việc nhóm cần hoàn tất

1. Review và merge PR sửa script nếu đồng ý; tải lại main mới rồi thử build/chạy trên Windows bằng JDK đầy đủ.
2. Thử nhanh giao diện: tìm phòng, đặt hợp lệ, lỗi trùng và hủy; dùng ngày tương lai.
3. Hoàn thiện báo cáo theo đề, sơ đồ lớp khớp source, ít nhất ba tình huống test và bảng đóng góp thực tế.
4. Bổ sung slide/video theo hồ sơ đã thực hiện; xác nhận yêu cầu nộp cuối cùng của giảng viên.
5. Đảm bảo giảng viên có quyền xem repo Private hoặc chọn cách cung cấp được thầy chấp nhận; không mặc định chỉ gửi link là xem được.
