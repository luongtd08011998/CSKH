# API Spec gửi Mobile App

## 1. GET `/api/v1/qlkh/customer/articles/maintenance`

Danh sách bài viết bảo trì cấp nước (tag "BaoTri-CupNuoc").

### Request

- **Method:** GET
- **Auth:** Không cần token (public)
- **Query params:**
  - `page` (optional, default 0) — trang hiện tại
  - `size` (optional, default 10) — số item mỗi trang

### Response

```json
{
  "statusCode": 200,
  "message": "Lấy danh sách bài viết bảo trì cấp nước thành công",
  "data": {
    "meta": {
      "page": 0,
      "size": 10,
      "totalPages": 2,
      "totalElements": 15
    },
    "result": [
      {
        "id": 1,
        "title": "Thông báo bảo trì cấp nước khu vực...",
        "slug": "thong-bao-bao-tri-cap-nuoc",
        "content": "Nội dung chi tiết bài viết...",
        "thumbnail": "https://example.com/image.jpg",
        "type": 0,
        "views": 120,
        "active": 1,
        "author": { "id": 1, "name": "Admin" },
        "category": { "id": 3, "name": "Tin tức" },
        "tags": [{ "id": 5, "name": "BaoTri-CupNuoc" }],
        "createdAt": "2026-04-29T03:00:00Z",
        "updatedAt": "2026-04-29T03:00:00Z"
      }
    ]
  }
}
```

### Ví dụ gọi

```
GET /api/v1/qlkh/customer/articles/maintenance?page=0&size=10
```

---

## 2. GET `/api/v1/qlkh/customer/notifications`

Lấy danh sách thông báo (đã cập nhật, thêm trường `url`).

### Thay đổi so với trước

- Mỗi notification object **thêm trường `url`** (String hoặc null)

### Response

```json
{
  "statusCode": 200,
  "message": "Lấy danh sách thông báo thành công",
  "data": [
    {
      "id": 1,
      "customerId": 123,
      "title": "Thông báo nâng cấp đồng hồ nước",
      "content": "Nội dung bài viết...",
      "type": "GENERAL",
      "isRead": false,
      "createdAt": "2026-04-28T10:00:00",
      "referenceId": 456,
      "isSystem": true,
      "url": "http://localhost:3000/news/thong-bao-nang-cap-dong-ho-nuoc"
    }
  ]
}
```

### Luật `url`

| Loại notification                                      | `url`                               |
| ------------------------------------------------------ | ----------------------------------- |
| System notification có `referenceId` (đi kèm bài viết) | `http://localhost:3000/news/{slug}` |
| System notification không có `referenceId`             | `null`                              |
| Notification cá nhân (INVOICE, PAYMENT)                | `null`                              |

### Khi nào dùng `url`

- Khi user tap vào notification → nếu `url` != null, mở webview/Deep link đến `url`
- Nếu `url` == null, mở màn hình chi tiết trong app (hóa đơn, thanh toán...)
