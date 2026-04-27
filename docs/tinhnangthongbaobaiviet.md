# Tài Liệu Tích Hợp Mobile App - Tính Năng Thông Báo Bài Viết

Tài liệu này mô tả các thay đổi về logic và API phía Backend liên quan đến tính năng **Gửi thông báo (Push Notification) khi có bài viết nổi bật mới**. Vui lòng cập nhật ứng dụng Mobile để đảm bảo tính năng hoạt động đồng bộ.

---

## 1. Đăng ký nhận thông báo chung (FCM Topic)

Để tối ưu hiệu năng gửi tin cho hàng chục ngàn user cùng lúc, hệ thống đổi sang dùng cấu trúc **Firebase Topics** thay vì dùng Device Token Multicast cho các bản tin chung.

> [!IMPORTANT]
> **Yêu cầu:** Ngay khi User mở App hoặc đăng nhập thành công, App cần gọi hàm subscribe vào topic `general_news`.

**Ví dụ Flutter / Kotlin:**
```kotlin
// Android (Kotlin)
FirebaseMessaging.getInstance().subscribeToTopic("general_news")
    .addOnCompleteListener { task ->
        var msg = "Subscribed"
        if (!task.isSuccessful) {
            msg = "Subscribe failed"
        }
        Log.d("FCM", msg)
    }
```

```dart
// Flutter
await FirebaseMessaging.instance.subscribeToTopic('general_news');
```

---

## 2. Xử lý Data Payload từ Push Notification

Khi có bài viết mới, Firebase sẽ đẩy về thiết bị một thông báo với cấu trúc `data payload` bổ sung nhằm giúp App tự động điều hướng (Deep link) vào màn hình chi tiết bài viết.

**Cấu trúc dữ liệu nhận được trong `data`:**
```json
{
  "type": "ARTICLE",
  "referenceId": "123"
}
```

- **`type`**: Loại thông báo. Phân biệt với hoá đơn (`INVOICE`) hay thanh toán (`PAYMENT`).
- **`referenceId`**: ID của bài viết. Mobile dùng ID này để gọi API lấy chi tiết bài viết và mở màn hình.

---

## 3. Cập Nhật Model: API Danh Sách Thông Báo

**Endpoint:** `GET /api/v1/qlkh/customer/notifications`

Danh sách thông báo hiện tại đã được **gộp chung** cả thông báo cá nhân (Tiền nước) và thông báo hệ thống (Tin tức chung). Do đó Model trả về có thêm 2 trường mới.

**JSON Response (Minh hoạ):**
```diff
 [
   {
     "id": 1,
     "customerId": 999,
     "title": "Bài viết nổi bật mới",
     "content": "Chính sách giá nước thay đổi năm 2026",
     "type": "ARTICLE",
     "isRead": false,
     "createdAt": "2026-04-25T10:00:00",
+    "referenceId": 45,
+    "isSystem": true
   }
 ]
```

- **`referenceId` (Long / Nullable):** Mã tham chiếu (VD: ID của bài viết). Nếu thông báo là Tiền nước, trường này có thể `null`.
- **`isSystem` (Boolean):** Cờ phân biệt. `true` = Thông báo hệ thống dùng chung, `false` = Thông báo riêng của user.

---

## 4. Cập Nhật API: Đánh Dấu Đã Đọc

Do thông báo hệ thống và thông báo cá nhân lưu ở 2 bảng khác nhau dưới DB, khi gọi API Đánh dấu đã đọc, Mobile bắt buộc phải gửi kèm cờ `isSystem` để Backend phân biệt.

**Endpoint:** `POST /api/v1/qlkh/customer/notifications/read`

**Request Body (Mới):**
```json
{
  "ids": [1],
  "isSystem": true
}
```

> [!WARNING]
> **Logic Xử Lý:**
> - Nếu người dùng click vào một item có `isSystem: true` (ví dụ bài viết tin tức), hãy gửi Request với `"isSystem": true`.
> - Nếu người dùng click vào một item hoá đơn thông thường (`isSystem: false` hoặc `null`), hãy gửi Request với `"isSystem": false`.
> - Trường hợp **"Đánh dấu tất cả là đã đọc"**: App cần gọi API này **2 lần** (hoặc Backend sẽ cung cấp API gộp nếu cần sau này). Một lần với `{"ids": [], "isSystem": false}` và một lần với `{"ids": [], "isSystem": true}`.
