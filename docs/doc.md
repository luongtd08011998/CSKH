Xây dựng app đa nền tảng (Android/iOS) theo Clean Architecture + MVVM/MVI với Ktor gọi API, Koin DI, multiplatform-settings lưu pre-fill form, và Jetbrains Navigation Compose. Session xác thực được giữ in-memory (SessionManager) — tồn tại khi process còn sống, mất khi clear task.
api:

1. Method Post: http://localhost/api/v1/qlkh/auth/login
   {
   "digiCode": "00400234",
   "phone": "02839846640"
   }

{
"data": {
"accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJjdXN0b21lcklkIjoyMzYxLCJzdWIiOiIwMDgwMDIxOSIsImRpZ2lDb2RlIjoiMDA4MDAyMTkiLCJleHAiOjE3NzU3MTgzNzcsImlhdCI6MTc3NTY4MzM3N30.0upL-aWmUmxYXeNxC_MNIvlq3qkib43hXWXRNln-I3s"
},
"message": "Đăng nhập thành công",
"statusCode": 200
}
2.Method Get: http://localhost/api/v1/qlkh/invoices
{
"data": {
"meta": {
"page": 1,
"pageSize": 20,
"pages": 7,
"total": 131
},
"result": [
{
"id": 2370,
"digiCode": "00800219",
"customerName": "NGUYỄN HỮU ĐỒNG",
"amount": 287600.0,
"envFee": 28760.0,
"taxFee": 14380.0,
"totalAmount": 330740.0,
"paymentStatus": 1,
"paymentStatusLabel": "Chưa thanh toán",
"oldVal": 626,
"newVal": 664
}
]
},
"message": "Lấy danh sách hóa đơn thành công",
"statusCode": 200
}
3.Method Get: http://localhost/api/v1/qlkh/invoices/9383
{
"data": {
"monthInvoiceId": 9383,
"customerId": 2361,
"yearMonth": "201503",
"amount": 99200.0,
"envFee": 9920.0,
"taxFee": 4960.0,
"invStatus": 1,
"paymentStatus": 1,
"paymentStatusLabel": "Đã thanh toán",
"createdDate": "20150129",
"startDate": "",
"endDate": "",
"oldVal": 690,
"newVal": 704,
"waterMeterSerial": "",
"numOfHouseHold": 1
},
"message": "Lấy chi tiết hóa đơn thành công",
"statusCode": 200
}
4.Method Get:
"data": {
"id": 9383,
"digiCode": "00800219",
"customerName": "NGUYỄN HỮU ĐỒNG",
"amount": 99200.0,
"envFee": 9920.0,
"taxFee": 4960.0,
"totalAmount": 114080.0,
"paymentStatus": 1,
"paymentStatusLabel": "Chưa thanh toán",
"oldVal": 690,
"newVal": 704
},
"message": "Lấy chi tiết hóa đơn thành công",
"statusCode": 200
}
thiết kế giao diẹn theo hướng hiện đại, thân thiện với khách hàng và phù hợp với công ty Nước của chúng tôi

- Man hình đăng nhập(có logo cty + tên công ty p dưới Công ty TNHH Cấp nước Tóc Tiên dưới cho nhập api mà tôi cung cấp)
  +SAu đó màn hình O trên là tên CTY xuống là logo bên trái và thông tin của cty o bên phải (địa chỉ(sẽ co icon địa chỉ bấm vào sẽ trỏ ra googlemap), sdt(có icon sdt bấm sẽ chuyển tiêos ra cuộc goi), email(co icon email khi bâm chuyen tiep ra gmail) dưới cac dịch vụ se chia thanh nhieu compóse(hóa đơn đẻ tra cuu hóa đơn đã có api tra cuu hoa đơn), bảng giá nước , giới thiệu)
