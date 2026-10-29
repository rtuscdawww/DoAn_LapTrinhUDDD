# Kế hoạch Hiển thị & Điều hướng các Màn hình VLUFind

Yêu cầu từ người dùng: Hiển thị các màn hình ứng dụng VLUFind (Trang chủ, Đăng tin, Cá nhân, Chi tiết bài đăng) với thanh điều hướng (Bottom Navigation Bar) để người dùng dễ dàng chuyển đổi giữa các màn hình.

---

## Các thay đổi đề xuất

### 1. Thành phần Giao diện & Màn hình mới

#### [NEW] [CreatePostScreen.kt](file:///C:/Users/ADMIN/AndroidStudioProjects/DoAn_LapTrinhUDDD/app/src/main/java/com/example/vlufind/ui/screens/CreatePostScreen.kt)
- Màn hình form đăng tin bài thất lạc hoặc nhặt được:
  - Chọn loại tin: **Cần tìm** (LOST) hoặc **Nhặt được** (FOUND).
  - Nhập Tiêu đề bài đăng, Danh mục đồ vật (Ví, Thẻ sinh viên, Điện thoại, Balo...), Địa điểm (Ví dụ: Thư viện tầng 6, Căn tin, Sảnh tòa A...).
  - Nhập Mô tả chi tiết.
  - Nhập Thông tin liên hệ (Họ tên, MSSV, SĐT).
  - Nút **Đăng tin** thêm bài viết vào danh sách dữ liệu.

#### [NEW] [ProfileScreen.kt](file:///C:/Users/ADMIN/AndroidStudioProjects/DoAn_LapTrinhUDDD/app/src/main/java/com/example/vlufind/ui/screens/ProfileScreen.kt)
- Màn hình trang cá nhân người dùng:
  - Hiển thị thông tin sinh viên (Họ tên, MSSV, SĐT, Số bài đã đăng).
  - Danh sách các bài đăng của sinh viên này.
  - Chức năng đánh dấu bài đăng là **Đã hoàn tất** (Đã tìm thấy / Đã trả lại đồ).

#### [NEW] [PostDetailDialog.kt](file:///C:/Users/ADMIN/AndroidStudioProjects/DoAn_LapTrinhUDDD/app/src/main/java/com/example/vlufind/ui/components/PostDetailDialog.kt)
- Dialog / Bottom Sheet chi tiết bài đăng khi người dùng nhấn vào thẻ tin đăng (`PostCard`):
  - Thông tin đầy đủ về đồ vật bị mất / nhặt được.
  - Vị trí cụ thể & thời gian đăng.
  - Thông tin người đăng + Nút gọi điện / liên hệ nhanh.

#### [MODIFY] [PostCard.kt](file:///C:/Users/ADMIN/AndroidStudioProjects/DoAn_LapTrinhUDDD/app/src/main/java/com/example/vlufind/ui/components/PostCard.kt)
- Bổ sung sự kiện `onClick` để mở dialog xem chi tiết tin đăng.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/ADMIN/AndroidStudioProjects/DoAn_LapTrinhUDDD/app/src/main/java/com/example/vlufind/ui/screens/HomeScreen.kt)
- Nhận danh sách bài đăng từ bộ nhớ ứng dụng.
- Tích hợp sự kiện xem chi tiết bài đăng.

#### [NEW] [MainScreen.kt](file:///C:/Users/ADMIN/AndroidStudioProjects/DoAn_LapTrinhUDDD/app/src/main/java/com/example/vlufind/ui/screens/MainScreen.kt)
- Màn hình chính tích hợp **Bottom Navigation Bar** gồm 3 mục:
  1. **Trang chủ**: Màn hình `HomeScreen` (Tìm kiếm, lọc danh mục, xem danh sách bài đăng).
  2. **Đăng tin**: Màn hình `CreatePostScreen` (Tạo tin mới).
  3. **Cá nhân**: Màn hình `ProfileScreen` (Quản lý thông tin & bài viết cá nhân).

#### [MODIFY] [MainActivity.kt](file:///C:/Users/ADMIN/AndroidStudioProjects/DoAn_LapTrinhUDDD/app/src/main/java/com/example/vlufind/MainActivity.kt)
- Cập nhật `MainActivity` để khởi chạy `MainScreen` với thanh điều hướng đầy đủ các màn hình.

---

## Kế hoạch kiểm tra (Verification Plan)

### Automated Build & Render Test
- Chạy `gradle_build` để đảm bảo ứng dụng biên dịch hoàn toàn không lỗi.
- Gọi `render_compose_preview` cho `MainScreenPreview`, `CreatePostScreenPreview`, `ProfileScreenPreview` để kiểm tra render visual của từng màn hình.
