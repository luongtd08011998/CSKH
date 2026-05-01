Backend đã hoàn thiện tính năng thông báo khi Admin phản hồi/đổi trạng thái phản ánh. Mobile cần xử lý đúng theo spec dưới đây.

1. Xử lý Push Notification (FCM)
Khi nhận được push notification từ FCM, kiểm tra field data trong payload:

json
{
  "notification": {
    "title": "Phản ánh của bạn đã được cập nhật",
    "body": "PHKH001-001: Đang xử lý"
  },
  "data": {
    "type": "FEEDBACK",
    "referenceId": "123"
  }
}
Logic xử lý:

Khi nhận FCM push:
  - Đọc data["type"]
  - Nếu type == "FEEDBACK":
      → Lấy data["referenceId"] (là feedbackId kiểu Long/Int)
      → Navigate đến FeedbackDetailScreen(id = referenceId)
  - Nếu type == "INVOICE" hoặc "PAYMENT":
      → Điều hướng về màn hình danh sách hóa đơn (như hiện tại)
2. Các loại type trong data payload
type	Ý nghĩa	referenceId có không?	Điều hướng
FEEDBACK	Admin đổi trạng thái hoặc gửi reply	Có (feedbackId)	FeedbackDetailScreen(id)
INVOICE	Hóa đơn tháng mới	Không	InvoiceListScreen
PAYMENT	Thanh toán thành công	Không	InvoiceListScreen
3. Các trường hợp type=FEEDBACK
Backend sẽ gửi push khi:

Hành động của Admin	Nội dung thông báo
Đổi trạng thái → PROCESSING	"PHKH001-001: Đang xử lý"
Đổi trạng thái → RESOLVED	"PHKH001-001: Đã xử lý xong"
Đổi trạng thái → REJECTED	"PHKH001-001: Không tiếp nhận"
Gửi reply	"PHKH001-001: <80 ký tự đầu nội dung reply>..."
4. Tab Thông báo — Phân biệt type
Khi lấy danh sách thông báo từ API GET /api/v1/qlkh/customer/notifications, mỗi item có:

json
{
  "id": 5,
  "type": "FEEDBACK",
  "referenceId": 123,
  "isSystem": false,
  "title": "Phản ánh của bạn đã được cập nhật",
  "content": "PHKH001-001: Đang xử lý",
  "isRead": false,
  "createdAt": "2026-05-02T03:30:00"
}
Khi user tap vào thông báo trong danh sách:

if (type == "FEEDBACK" && referenceId != null):
    → navigate FeedbackDetailScreen(id = referenceId)
else if (type == "INVOICE" || type == "PAYMENT"):
    → navigate InvoiceListScreen
5. Đánh dấu đã đọc sau khi tap
Sau khi navigate xong, gọi API mark-as-read:

PATCH /api/v1/qlkh/customer/notifications/mark-read
Body: { "ids": [5], "isSystem": false }
6. Kiểm tra referenceId null-safe
⚠️ Lưu ý: referenceId trong FCM data là kiểu String (FCM chỉ truyền String). Mobile cần parse sang Long/Int trước khi dùng.

kotlin
// Kotlin example
val referenceId = data["referenceId"]?.toLongOrNull()
val type = data["type"] ?: ""
when (type) {
    "FEEDBACK" -> {
        referenceId?.let {
            navigate(FeedbackDetailScreen(feedbackId = it))
        }
    }
    "INVOICE", "PAYMENT" -> {
        navigate(InvoiceListScreen)
    }
}