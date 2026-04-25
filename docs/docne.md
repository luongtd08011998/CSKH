uyệt đối KHÔNG tự set ContentType khi dùng MultiPartFormDataContent Nếu trong hàm gọi API có dòng nào tự gán contentType hay set header thủ công thì phải xóa ngay lập tức. Ktor sẽ tự động gán ContentType và Boundary chuẩn khi bạn truyền MultiPartFormDataContent.
client.post("http://10.0.2.2:8080/api/v1/qlkh/customer/feedbacks") {
// ❌ LỖI 1: NẾU CÓ 1 TRONG 2 DÒNG NÀY THÌ PHẢI XÓA NGAY LẬP TỨC
// contentType(ContentType.MultiPart.FormData)
// header(HttpHeaders.ContentType, "multipart/form-data")

    // ✅ CHỈ CẦN TRUYỀN BODY LÀ ĐỦ:
    setBody(
        MultiPartFormDataContent(
            formData {
                append("issueType", "outage")
                append("location", "ASDSDSAD")
                append("description", "mat nuoc may ngay")

                // Đính kèm File: Bắt buộc phải khai báo ContentDisposition
                val file = File(uri.path)
                append("upload_image", file.readBytes(), Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "form-data; name=\"upload_image\"; filename=\"${file.name}\"")
                })
            }
        )
    )

} 2. Kiểm tra lại DefaultRequest (hoặc Plugin ContentNegotiation) Nếu trong lúc khởi tạo Ktor Client, bạn ấy có cài đặt mặc định mọi Request đều là JSON thì nó cũng sẽ đè mất Boundary của Multipart:

kotlin
val client = HttpClient(CIO) {
install(DefaultRequest) {
// ❌ Nếu chỗ này đang hardcode header("Content-Type", "application/json")
// thì khi gửi file bằng Multipart sẽ bị lỗi 400.
// Cần phải check xem Request này có phải là gửi file không trước khi set JSON.
}
}
