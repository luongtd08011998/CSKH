Chào Mobile Dev (KMP Compose team), dưới đây là tài liệu tích hợp tính năng Phản ánh dịch vụ từ phía Backend.

Hệ thống cung cấp 2 API phục vụ cho tính năng này:

API Gửi phản ánh (có hỗ trợ upload tối đa 5 ảnh).
API Lấy danh sách lịch sử phản ánh.
Yêu cầu chung: Cả 2 API đều cần truyền JWT Token vào Header (Authorization: Bearer <token>). Không cần truyền customerId hay digiCode vì Backend tự động lấy từ Token để chống mạo danh.

1. API Gửi phản ánh (Create Feedback)
   Vì API có hỗ trợ upload file gốc, bạn bắt buộc phải sử dụng multipart/form-data, KHÔNG dùng Raw JSON.

Method: POST
URL: /api/v1/qlkh/customer/feedbacks
Headers:
http
Authorization: Bearer <jwt_token>
Body (form-data):
Key Kiểu dữ liệu Bắt buộc Mô tả
issueType Text Có Phải là 1 trong 7 chuỗi sau (không phân biệt hoa/thường):
leak, quality, pressure, outage, billing, meter, other.
Truyền sai sẽ nhận lỗi HTTP 400.
location Text Có Địa điểm xảy ra sự cố.
description Text Có Mô tả chi tiết vấn đề.
images File Không Đính kèm ảnh (tối đa 5 ảnh). Tên key phải giống nhau (đều là images). Có thể gửi mảng rỗng nếu khách hàng không chọn ảnh.
✅ Response Thành công (HTTP 200 OK):

json
{
"statusCode": 200,
"message": "Gửi phản ánh thành công",
"data": {
"trackingCode": "PH03600668-001"
}
}
Note: Bạn lấy data.trackingCode để hiển thị trên màn hình "Gửi thành công".

❌ Response Lỗi thường gặp (HTTP 400 Bad Request):

json
{
"statusCode": 400,
"message": "Loại vấn đề không hợp lệ. Chỉ chấp nhận: leak, quality, pressure, outage, billing, meter, other",
"data": null
}
// Hoặc lỗi upload quá 5 ảnh, định dạng ảnh không hợp lệ... 2. API Lấy Lịch sử phản ánh (Get Feedbacks)
Lấy toàn bộ danh sách các phản ánh mà user này đã gửi (được sắp xếp mới nhất lên đầu).

Method: GET
URL: /api/v1/qlkh/customer/feedbacks
Headers:
http
Authorization: Bearer <jwt_token>
✅ Response Thành công (HTTP 200 OK):

json
{
"statusCode": 200,
"message": "Lấy danh sách phản ánh thành công",
"data": [
{
"id": 1,
"trackingCode": "PH03600668-001",
"issueType": "LEAK",
"location": "234 Nguyễn Trãi, Q1",
"description": "Ống nước vỡ chảy lênh láng",
"status": "PENDING",
"images": [
"/uploads/feedbacks/1714123456_anh1.jpg",
"/uploads/feedbacks/1714123458_anh2.jpg"
],
"createdAt": "2026-04-24T16:10:00.12345"
}
]
}
Giải thích các trường trả về:

status: Trạng thái xử lý của Backend. Thường sẽ có các giá trị: PENDING (Đang chờ xử lý), PROCESSING (Đang xử lý), RESOLVED (Đã giải quyết), REJECTED (Bị từ chối). Mobile có thể dùng map màu sắc (Ví dụ: PENDING -> Màu Vàng, RESOLVED -> Màu Xanh...).
images: Mảng string chứa link (path) tương đối của ảnh. Khi render trên app, bạn cần ghép chuỗi với BASE_URL của server (Ví dụ: https://api.domain.com + /uploads/feedbacks/...).
createdAt: Thời gian gửi phản ánh (chuẩn ISO-8601).
Lời khuyên cho KMP Compose (Ktor Client): Khi call API POST bằng Ktor, hãy dùng MultiPartFormDataContent và append("images", fileBytes, Headers.build {...}) để đính kèm nhiều ảnh đúng chuẩn.
