# My File — Ứng dụng Quản lý File Android

Ứng dụng quản lý file (file manager) trên Android, viết bằng Java, cho phép duyệt, thao tác (copy/move/xóa/đổi tên), xem nội dung, nén/giải nén và giấu file vào thư mục bảo mật ngay trong app.

## 1. Thông tin chung

| | |
|---|---|
| Package | `com.example.myfile` |
| Ngôn ngữ | Java |
| Min SDK | 24 (Android 7.0) |
| Target / Compile SDK | 34 (Android 14) |
| Build system | Gradle (Kotlin DSL) |

## 2. Tính năng

- **Duyệt file / thư mục**: xem theo dạng list/grid, breadcrumb, sắp xếp theo tên/kích thước/ngày/loại, xem theo bộ nhớ (storage) hoặc thư mục nhanh (quick folder).
- **Thao tác file**: copy, move, xóa, đổi tên, tạo file/thư mục mới, chọn nhiều (multi-select).
- **Thùng rác (Trash)**: khôi phục hoặc xóa vĩnh viễn file đã xóa.
- **Tìm kiếm (Search)**: tìm file theo tên trong bộ nhớ thiết bị, có lịch sử tìm kiếm gần đây.
- **Thư mục bảo mật (Private Vault)**: đặt mật khẩu, di chuyển file/thư mục vào vault để ẩn khỏi trình duyệt file chính, khôi phục lại vị trí gốc khi cần.
- **Xem nội dung file** (mở ngay trong app, không cần rời sang app khác):
  - Ảnh: xem full màn hình, pinch-to-zoom, vuốt chuyển ảnh khác trong cùng thư mục, chia sẻ (share).
  - Video: phát bằng ExoPlayer, vuốt trái/phải để chuyển video khác trong cùng thư mục.
  - EXIF: xem metadata của ảnh (ngày chụp, camera, khẩu độ, ISO, GPS...).
  - Text: xem nhanh nội dung file `.txt`.
  - Các loại file khác (PDF, APK, DOCX...): mở bằng app ngoài qua `FileProvider` + `ACTION_VIEW`.
- **Nén / Giải nén**:
  - Nén nhiều file/thư mục cùng lúc thành 1 file `.zip`, tự bỏ qua file ẩn.
  - Tự động đổi tên nếu file/thư mục đích đã tồn tại (không ghi đè, không merge).
  - Xác nhận đích đến trước khi giải nén.
  - Chống **Zip Slip** (entry độc hại trỏ ra ngoài thư mục đích) — hủy toàn bộ thao tác và dọn dẹp file/thư mục dở dang nếu phát hiện.

## 3. Kiến trúc & cấu trúc thư mục

```
app/src/main/java/com/example/myfile/
├── MainActivity.java              # Host chính: toolbar, danh sách file, điều phối các thao tác
├── FolderPickerActivity.java      # Chọn thư mục đích (copy/move)
├── TrashActivity.java             # Màn hình thùng rác
├── SearchActivity.java            # Màn hình tìm kiếm
│
├── data/
│   ├── model/FileItem.java            Model 1 file/thư mục
│   ├── repository/                    FileRepository — đọc dữ liệu file từ bộ nhớ
│   ├── storage/                       StorageHelper, danh sách ổ đĩa/thư mục nhanh
│   ├── trash/TrashManager.java        Quản lý thùng rác
│   └── vault/PrivateVaultManager.java Quản lý thư mục bảo mật (mật khẩu, ẩn/khôi phục file)
│
├── domain/operation/              # Các thao tác file, theo pattern Command
│   ├── FileOperation.java             ⚑ interface — execute() dùng chung
│   ├── CopyOperation / MoveOperation / DeleteOperation / RenameOperation / SoftDeleteOperation
│   ├── CreateFileOperation / CreateFolderOperation
│   ├── ZipOperation.java              nén nhiều nguồn thành 1 .zip, tự tránh trùng tên
│   └── UnzipOperation.java            giải nén, tự tránh trùng tên, chống Zip Slip
│
├── feature/viewer/
│   ├── ViewerFactory.java             ⚑ Factory — chọn viewer phù hợp theo MIME type của file
│   ├── image/ImageViewerActivity, ImagePagerAdapter      Glide + PhotoView + ViewPager2
│   ├── video/VideoPlayerActivity, VideoPagerAdapter      ExoPlayer, vuốt chuyển video
│   ├── exif/ExifViewerActivity, ExifReader, ExifAdapter  đọc metadata qua androidx.exifinterface
│   └── text/TextViewerActivity                           xem nhanh file text
│
├── ui/
│   ├── main/           FileAdapter, FileListHelper, SortMode — danh sách & sắp xếp file
│   ├── storage/        StorageAdapter, QuickFolderAdapter — chọn ổ đĩa / thư mục nhanh
│   └── vault/           PrivateVaultActivity — màn hình thư mục bảo mật
│
└── utils/
    ├── FileUtils.java              content URI qua FileProvider, sinh tên không trùng
    ├── MimeUtils.java              phân loại file theo phần mở rộng → MIME/category
    └── ThumbnailLoader.java        load thumbnail cho ảnh/video

app/src/main/res/
├── layout/                        activity_*.xml, item_*.xml, dialog_*.xml
├── xml/file_paths.xml             khai báo thư mục cho FileProvider
└── values/                        strings, colors, themes
```

### Pattern chính được dùng
- **Factory** (`ViewerFactory`): route file sang đúng Activity xem nội dung dựa theo MIME type.
- **Command** (`FileOperation` + các lớp con): mỗi thao tác file là 1 class riêng cùng implement `execute()`, `MainActivity` chỉ gọi qua interface chung, chạy nền qua `runOperationWithProgress()`.
- **Adapter cho ViewPager2**: `ImagePagerAdapter` / `VideoPagerAdapter` cấp view cho từng trang, cho phép vuốt qua lại giữa các file cùng loại trong một thư mục.

## 4. Quyền (Permissions)

```xml
READ_EXTERNAL_STORAGE   (tối đa API 32)
WRITE_EXTERNAL_STORAGE  (tối đa API 28)
MANAGE_EXTERNAL_STORAGE (Android 11+, truy cập toàn bộ bộ nhớ)
```
Chia sẻ file với app khác được thực hiện an toàn qua `FileProvider` (authority: `${applicationId}.fileprovider`), không dùng `file://` URI trực tiếp.

## 5. Thư viện sử dụng

| Thư viện | Mục đích |
|---|---|
| AndroidX AppCompat, Material, RecyclerView | UI nền tảng |
| Glide | Load ảnh/thumbnail |
| PhotoView | Pinch-to-zoom khi xem ảnh |
| ViewPager2 | Vuốt chuyển ảnh/video trong cùng thư mục |
| ExoPlayer 2.19.1 | Phát video |
| androidx.exifinterface | Đọc metadata EXIF của ảnh |

## 6. Build & chạy project

1. Cài Android Studio (Giraffe trở lên khuyến nghị) và JDK 8+.
2. Mở project bằng Android Studio → để Gradle tự sync (`compileSdk = 34`, cần Android SDK 34 đã cài, và repo `jitpack.io` cho PhotoView).
3. Cắm thiết bị thật hoặc dùng Emulator API ≥ 24.
4. Chạy trực tiếp bằng nút Run, hoặc build APK bằng:
   ```
   ./gradlew assembleDebug
   ```
   File APK tạo ra tại `app/build/outputs/apk/debug/`.
5. Lần đầu mở app trên Android 11+, cần cấp quyền "Quản lý toàn bộ file" (All files access) khi được yêu cầu.

## 7. Lưu ý khi phát triển tiếp

- Khi thêm Activity mới, nhớ khai báo trong `AndroidManifest.xml` (thiếu dòng này là nguyên nhân phổ biến nhất gây crash `ActivityNotFoundException`).
- Khi thêm thư mục cần truy cập qua `FileProvider`, cập nhật thêm trong `res/xml/file_paths.xml`.
- Toàn bộ text hiển thị cho người dùng (UI, Toast, dialog) dùng tiếng Anh để đồng bộ trong toàn app; comment trong code có thể giữ tiếng Việt cho dev.
- ⚠️ `PrivateVaultManager` hiện lưu mật khẩu vault dạng plaintext trong `SharedPreferences` — nên hash (vd SHA-256 + salt) trước khi công bố/nộp bài.

## 8. Phân công (tham khảo)

| Thành viên | Phạm vi |
|---|---|
| A | `data/`, `domain/operation/` (Copy, Move, Delete, Rename), Trash |
| B | `ui/main/`, `ui/storage/` (duyệt file, danh sách, sắp xếp, ổ đĩa/thư mục nhanh) |
| C | `feature/viewer/` (xem ảnh/video/EXIF/text) + `domain/operation/ZipOperation`, `UnzipOperation` (nén/giải nén) |
| — | `data/vault/`, `ui/vault/` (Thư mục bảo mật) |
