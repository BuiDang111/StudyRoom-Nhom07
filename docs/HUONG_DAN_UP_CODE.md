# Hướng dẫn tải code bằng giao diện GitHub

## Trưởng nhóm làm trước

Repo đang Private. Vào Settings → Collaborators → Add people để mời đúng username GitHub của bốn thành viên. Nhóm chưa cung cấp các username này nên chưa có lời mời nào được gửi. Từng người cần chấp nhận lời mời trước khi tải lên nhánh chung.

## Mỗi thành viên

1. Giải nén toàn bộ ZIP StudyRoom được nhóm chốt.
2. Xem DANH_SACH_FILE.md, lấy đúng file của phần được giao. Chuẩn bị một thư mục chứa file và giữ nguyên đường dẫn tính từ bên trong StudyRoom. Ví dụ backend/src/main/java/com/group07/roombooking/model/Room.java.
3. Mở repo, ở bộ chọn nhánh đang ghi main, chọn nhánh họ tên mình.
4. Tại trang gốc của repo, chọn Add file → Upload files. Kéo các thư mục đã chuẩn bị vào. Không kéo cả thư mục ngoài cùng StudyRoom và không tải cả ZIP.
5. Kiểm tra danh sách đang tải có tiền tố backend/ hoặc frontend/ đúng vị trí. Nếu mất thư mục, hủy và chuẩn bị lại. Với backend có thể phải tải nhiều lượt nếu giao diện báo quá nhiều file.
6. Commit với nội dung như: Nhập phần 2 mô hình OOP và tính phí từ bản mã nguồn nhóm chốt. Không ghi đã tự viết mới nếu chỉ nhập code.
7. Tạo Pull Request: base main, compare nhánh của mình. Nêu số phần, các file và việc đã kiểm tra. Chưa merge đủ thì đánh dấu chưa chạy toàn bộ.
8. Trưởng nhóm xem Files changed và merge lần lượt từng PR. Không tải lại toàn bộ dự án vào nhánh khác.

## Những file không tải

- Giữ README.md và .gitignore mới của repo, không chép đè bằng hai file cùng tên trong ZIP.
- Không tải runtime/, target/, node_modules/, frontend/dist/, ZIP, JAR hoặc dữ liệu cá nhân ngoài phạm vi dự án.
- Không tải tài liệu gốc của thầy trong tai-lieu-goc/ ở bước nhập source.
- Không tải XEM_GIAO_DIEN_OFFLINE.html vì đây là bản demo riêng.
- Phải giữ frontend/public/studyroom.png và backend/src/main/resources/static/ trong phần được giao. Static đang là giao diện đã build để backend phục vụ; không xóa vì nhầm với frontend/dist/.

## Kiểm tra sau khi đủ 5 phần

- Có pom.xml và Maven wrapper trong backend, package.json và package-lock.json trong frontend.
- Nếu cần build lại frontend: vào frontend chạy npm ci rồi npm run build; kiểm tra cấu hình/script của dự án để cập nhật bản static backend đúng cách.
- Vào backend chạy mvnw.cmd test trên Windows. Chỉ công bố số test đã đạt sau khi xem kết quả thực tế.
- Build và chạy theo script của bản nguồn, kiểm tra script cần runtime/studyroom.jar hoặc dữ liệu nào; JAR không nằm trong repo khung này. Có thể đính kèm JAR trong Release sau khi build.
- Chạy thử tìm phòng, đặt thành công, từ chối trùng, hủy và tải lại.

Chưa bật tự động CI ở giai đoạn upload từng phần vì các phần chưa đầy đủ. Chỉ thêm sau khi ghép chạy được.
