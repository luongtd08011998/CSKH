[PROMPT START] Bạn là một Senior Android Developer. Hệ thống Backend Spring Boot của chúng tôi đã hoàn thiện luồng Xác thực và Push Notification qua Firebase Cloud Messaging (FCM). Hãy tích hợp tính năng nhận thông báo vào App Android với các yêu cầu sau:

1. Tích hợp Firebase SDK:

Cấu hình file google-services.json vào project Android.
Thêm các dependency cần thiết cho firebase-messaging. 2. Luồng đăng ký Device Token (FCM Token):

Trigger: Ngay sau khi user Login thành công (lấy được Access Token) HOẶC khi mở app lên mà đã có sẵn Access Token hợp lệ.
Action: Lấy Device Token hiện tại bằng lệnh FirebaseMessaging.getInstance().getToken().
API Call: Gửi Device Token lên Backend thông qua API sau:
Endpoint: POST /api/v1/qlkh/customer/device/register
Headers:
Content-Type: application/json
Authorization: Bearer {ACCESS_TOKEN}
Request Body (JSON):
json
{
"deviceToken": "chuỗi-token-fcm-lấy-được-từ-firebase",
"platform": "ANDROID"
} 3. Xử lý nhận Push Notification:

Tạo class kế thừa FirebaseMessagingService.
Override onNewToken(String token): Nếu token thay đổi trong quá trình sử dụng app, tự động gọi lại API register ở bước 2 để update token mới lên Backend.
Override onMessageReceived(RemoteMessage remoteMessage):
Backend đang gửi notification dưới dạng Notification Message (có trường title và body).
Nếu app ở Background/Killed state, hệ điều hành Android sẽ tự hiển thị notification lên khay hệ thống (System Tray).
Nếu app ở Foreground (đang mở), hãy tự build một Notification và hiển thị (dùng NotificationCompat.Builder). 4. Màn hình danh sách thông báo (Tùy chọn - nếu làm UI):

Backend có cung cấp API lấy danh sách lịch sử thông báo đã nhận:
Lấy danh sách: GET /api/v1/qlkh/customer/notifications (kèm Bearer Token).
Đánh dấu đã đọc: POST /api/v1/qlkh/customer/notifications/read (Body: { "ids": [1, 2] } hoặc không truyền body để đánh dấu đọc tất cả).
Hãy viết code thực thi các logic trên (Kotlin/Java tùy project) và chú ý xử lý các exception (như lỗi mạng, lỗi lấy token). [PROMPT END]
