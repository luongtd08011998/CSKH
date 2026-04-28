# Mobile App — Chức năng Phản ánh Dịch vụ Nước

## Tổng quan

Mobile App cho phép khách hàng (KH) gửi phản ánh về dịch vụ nước (rò rỉ, chất lượng nước, cắt nước...) kèm ảnh, và theo dõi trạng thái xử lý + phản hồi từ staff.

---

## 1. Auth

Login QLKH riêng, khác với Admin system.

|              |                                                              |
| ------------ | ------------------------------------------------------------ |
| **Endpoint** | `POST /api/v1/qlkh/auth/login`                               |
| **Body**     | `{ "digiCode": "KH001", "phone": "0901234567" }`             |
| **Response** | `{ "statusCode": 200, "data": { "accessToken": "eyJ..." } }` |

> Header: `Authorization: Bearer <token>` cho mọi API phía dưới.

---

## 2. API cần tích hợp

### 2.1. Gửi phản ánh

```
POST /api/v1/qlkh/customer/feedbacks
Content-Type: multipart/form-data
Authorization: Bearer <token>
```

| Field                                     | Type   | Required | Mô tả                                                                |
| ----------------------------------------- | ------ | -------- | -------------------------------------------------------------------- |
| `issueType`                               | String | Có       | `LEAK`, `QUALITY`, `PRESSURE`, `OUTAGE`, `BILLING`, `METER`, `OTHER` |
| `location`                                | String | Có       | Địa chỉ/vị trí                                                       |
| `description`                             | String | Có       | Mô tả chi tiết                                                       |
| `images` hoặc `image` hoặc `upload_image` | File[] | Không    | Ảnh (hỗ trợ nhiều tên field)                                         |

**Response 200:**

```json
{
  "statusCode": 200,
  "message": "Gửi phản ánh thành công",
  "data": { "trackingCode": "PHKH001-001" }
}
```

---

### 2.2. Danh sách phản ánh của KH

```
GET /api/v1/qlkh/customer/feedbacks
Authorization: Bearer <token>
```

**Response 200:**

```json
{
  "statusCode": 200,
  "message": "Lấy danh sách phản ánh thành công",
  "data": [
    {
      "id": 1,
      "trackingCode": "PHKH001-001",
      "issueType": "LEAK",
      "location": "123 Nguyễn Huệ, Quận 1",
      "description": "Ống nước rò rỉ trước nhà từ 2 ngày nay...",
      "status": "PROCESSING",
      "images": ["/uploads/feedbacks/1777238507236_46.jpg"],
      "createdAt": "2026-04-28T10:00:00"
    }
  ]
}
```

---

### 2.3. Chi tiết phản ánh kèm phản hồi từ staff

```
GET /api/v1/qlkh/customer/feedbacks/{id}
Authorization: Bearer <token>
```

**Response 200:**

```json
{
  "statusCode": 200,
  "message": "Lấy chi tiết phản ánh thành công",
  "data": {
    "id": 1,
    "trackingCode": "PHKH001-001",
    "issueType": "LEAK",
    "location": "123 Nguyễn Huệ, Quận 1, TP.HCM",
    "description": "Ống nước rò rỉ trước nhà từ 2 ngày nay...",
    "status": "PROCESSING",
    "images": ["/uploads/feedbacks/1777238507236_46.jpg"],
    "replies": [
      {
        "id": 1,
        "staff": {
          "id": 5,
          "name": "Trần Văn B",
          "email": "tranb@company.com",
          "avatar": "/uploads/avatars/staff5.jpg"
        },
        "content": "Chúng tôi đã tiếp nhận và sẽ xử lý trong 24h.",
        "createdAt": "2026-04-28T11:00:00"
      },
      {
        "id": 2,
        "staff": {
          "id": 5,
          "name": "Trần Văn B",
          "email": "tranb@company.com",
          "avatar": "/uploads/avatars/staff5.jpg"
        },
        "content": "Đã sửa xong ống rò rỉ. Vui lòng kiểm tra lại.",
        "createdAt": "2026-04-29T09:00:00"
      }
    ],
    "createdAt": "2026-04-28T10:00:00",
    "updatedAt": "2026-04-29T09:00:00"
  }
}
```

---

## 3. Enum Reference

### IssueType — hiển thị tiếng Việt trên UI

| Value      | Tiếng Việt         |
| ---------- | ------------------ |
| `LEAK`     | Rò rỉ nước         |
| `QUALITY`  | Chất lượng nước    |
| `PRESSURE` | Áp lực nước        |
| `OUTAGE`   | Cắt nước           |
| `BILLING`  | Thanh toán/Hóa đơn |
| `METER`    | Đồng hồ nước       |
| `OTHER`    | Khác               |

### FeedbackStatus — badge màu trên UI

| Value        | Tiếng Việt | Màu gợi ý  |
| ------------ | ---------- | ---------- |
| `PENDING`    | Chờ xử lý  | Vàng/Cam   |
| `PROCESSING` | Đang xử lý | Xanh dương |
| `RESOLVED`   | Đã xử lý   | Xanh lá    |
| `REJECTED`   | Đã từ chối | Đỏ         |

---

## 4. Screens cần xây dựng

### Screen 1: Form Gửi Phản Ánh

```
┌─────────────────────────────────┐
│  ← Gửi phản ánh         [GPS] │
├─────────────────────────────────┤
│                                 │
│  Loại vấn đề *                  │
│  ┌─────────────────────────┐   │
│  │ Chọn loại vấn đề    ▼   │   │
│  └─────────────────────────┘   │
│                                 │
│  Vị trí *                      │
│  ┌─────────────────────────┐   │
│  │ Nhập hoặc chọn GPS...   │   │
│  └─────────────────────────┘   │
│                                 │
│  Mô tả *                       │
│  ┌─────────────────────────┐   │
│  │                         │   │
│  │  Nhập mô tả chi tiết... │   │
│  │                         │   │
│  └─────────────────────────┘   │
│                                 │
│  Hình ảnh đính kèm             │
│  ┌───┐ ┌───┐ ┌───┐            │
│  │ + │ │   │ │   │            │
│  └───┘ └───┘ └───┘            │
│  (tối đa 5 ảnh)                │
│                                 │
│  ┌─────────────────────────┐   │
│  │       GỬI PHẢN ÁNH      │   │
│  └─────────────────────────┘   │
└─────────────────────────────────┘
```

**Functionality:**

- Dropdown/bottom sheet chọn `issueType` (7 loại, hiển thị tiếng Việt)
- Input location: manual nhập hoặc tích hợp GPS map picker
- Textarea nhập description (required)
- Image picker: chọn từ gallery hoặc chụp ảnh, tối đa 5 ảnh
- Upload via `multipart/form-data`, field name: `images`
- Sau khi gửi thành công: dialog hiển thị `trackingCode`, nút về danh sách

---

### Screen 2: Danh Sách Phản Ánh

```
┌─────────────────────────────────┐
│  Phản ánh của tôi               │
├─────────────────────────────────┤
│                                 │
│  ┌─────────────────────────┐   │
│  │ 🔵 Đang xử lý            │   │
│  │ PHKH001-001             │   │
│  │ Rò rỉ nước              │   │
│  │ 123 Nguyễn Huệ, Q1      │   │
│  │ 28/04/2026 10:00        │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 🟡 Chờ xử lý             │   │
│  │ PHKH001-003             │   │
│  │ Thanh toán/Hóa đơn      │   │
│  │ 456 Lê Lợi, Q3          │   │
│  │ 29/04/2026 08:30        │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 🟢 Đã xử lý             │   │
│  │ PHKH001-010             │   │
│  │ Đồng hồ nước            │   │
│  │ 789 Trần Hưng Đạo, Q5   │   │
│  │ 20/03/2026 14:00        │   │
│  └─────────────────────────┘   │
│                                 │
│        ┌───────────────┐        │
│        │  + Gửi mới    │        │
│        └───────────────┘        │
└─────────────────────────────────┘
```

**Functionality:**

- Load danh sách từ `GET /api/v1/qlkh/customer/feedbacks`
- Hiển thị: status badge (màu), trackingCode, issueType (tiếng Việt), location, createdAt
- Pull-to-refresh
- FAB "+ Gửi mới" → navigate to Screen 1
- Tap vào item → navigate to Screen 3

---

### Screen 3: Chi Tiết Phản Ánh

```
┌─────────────────────────────────┐
│  ← Chi tiết phản ánh            │
├─────────────────────────────────┤
│                                 │
│  Status: 🔵 Đang xử lý          │
│  Mã: PHKH001-001               │
│  Loại: Rò rỉ nước               │
│  Vị trí: 123 Nguyễn Huệ, Q1    │
│  Ngày gửi: 28/04/2026 10:00    │
│                                 │
│  Mô tả:                         │
│  Ống nước rò rỉ trước nhà      │
│  từ 2 ngày nay, nước chảy ra   │
│  đường rất lãng phí.            │
│                                 │
│  Hình ảnh:                      │
│  ┌─────┐ ┌─────┐               │
│  │ img │ │ img │  [xem tất cả] │
│  └─────┘ └─────┘               │
│                                 │
│  ── Phản hồi từ công ty ──     │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 👤 Trần Văn B (Staff)   │   │
│  │ 28/04/2026 11:00       │   │
│  │ Đã tiếp nhận, sẽ xử lý  │   │
│  │ trong 24h.              │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 👤 Trần Văn B (Staff)   │   │
│  │ 29/04/2026 09:00       │   │
│  │ Đã sửa xong ống rò rỉ. │   │
│  │ Vui lòng kiểm tra lại. │   │
│  └─────────────────────────┘   │
│                                 │
└─────────────────────────────────┘
```

**Functionality:**

- Gọi `GET /api/v1/qlkh/customer/feedbacks/{id}` khi tap vào item từ Screen 2
- Hiển thị full thông tin + status badge màu + issueType tiếng Việt
- Image gallery: tap để xem full screen
- Phần phản hồi: hiển thị danh sách replies từ staff (tên, avatar, nội dung, thời gian)
- Nếu chưa có reply: hiển thị "Chưa có phản hồi"

---

## 5. Base URL cho ảnh

URL ảnh trả về từ API là path tương đối `/uploads/feedbacks/xxx.jpg`. Mobile cần prepend base URL:

```
// Dev
http://<BE_IP>:8080/uploads/feedbacks/xxx.jpg

// Production
https://125.253.121.171/uploads/feedbacks/xxx.jpg
```

---

## 6. Lưu ý

- **Validation gửi phản ánh:** `issueType`, `location`, `description` bắt buộc. `issueType` phải là 1 trong 7 giá trị enum, nếu sai BE trả lỗi 400. Tối đa 5 ảnh, mỗi ảnh có giới hạn kích thước (xem cấu hình server).
- **Tracking code format:** `PH{digiCode}-{id}`, ví dụ `PHKH001-001` — lưu lại để KH tra cứu.
- **Security:** mọi endpoint đều cần `Authorization: Bearer <token>` từ QLKH login. Token hết hạn → BE trả 401 → redirect về login.
- **Empty state:** màn danh sách trống khi chưa có phản ánh → illustration + nút "Gửi phản ánh đầu tiên".
