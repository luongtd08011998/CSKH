package com.example.cskh.util

/**
 * Dịch thông báo lỗi tiếng Anh từ API server sang tiếng Việt thân thiện với người dùng.
 */
fun mapApiErrorToVietnamese(message: String?): String {
    if (message.isNullOrBlank()) return "Đã xảy ra lỗi. Vui lòng thử lại."

    val lower = message.lowercase().trim()

    return when {
        // Lỗi liên quan đến số điện thoại
        lower.contains("phone param is not correct") ||
        lower.contains("phone is not correct") ||
        lower.contains("invalid phone") ->
            "Số điện thoại không đúng. Vui lòng kiểm tra lại."

        lower.contains("phone") && lower.contains("required") ->
            "Vui lòng nhập số điện thoại."

        lower.contains("phone") && (lower.contains("format") || lower.contains("valid")) ->
            "Số điện thoại không hợp lệ. Vui lòng nhập đúng định dạng."

        // Lỗi liên quan đến mã khách hàng
        lower.contains("digicode") && lower.contains("not correct") ||
        lower.contains("digi_code") && lower.contains("not correct") ||
        lower.contains("invalid digicode") ||
        lower.contains("invalid digi_code") ->
            "Mã khách hàng không đúng. Vui lòng kiểm tra lại."

        lower.contains("digicode") && lower.contains("required") ||
        lower.contains("digi_code") && lower.contains("required") ->
            "Vui lòng nhập mã khách hàng."

        // Lỗi xác thực chung
        lower.contains("unauthorized") || lower.contains("401") ->
            "Thông tin đăng nhập không chính xác. Vui lòng kiểm tra lại mã khách hàng và số điện thoại."

        lower.contains("credentials") || lower.contains("authentication failed") ->
            "Sai thông tin đăng nhập. Vui lòng thử lại."

        lower.contains("account") && lower.contains("locked") ->
            "Tài khoản đã bị khóa. Vui lòng liên hệ hotline để được hỗ trợ."

        lower.contains("account") && (lower.contains("not found") || lower.contains("not exist")) ->
            "Tài khoản không tồn tại. Vui lòng kiểm tra lại thông tin."

        // Lỗi mạng / server
        lower.contains("timeout") || lower.contains("timed out") ->
            "Kết nối quá chậm. Vui lòng kiểm tra mạng và thử lại."

        lower.contains("no internet") || lower.contains("network") || 
        lower.contains("unable to resolve host") || lower.contains("connect") ->
            "Không có kết nối mạng. Vui lòng kiểm tra WiFi hoặc 4G và thử lại."

        lower.contains("500") || lower.contains("internal server") ->
            "Hệ thống đang bảo trì. Vui lòng thử lại sau ít phút."

        lower.contains("503") || lower.contains("service unavailable") ->
            "Hệ thống tạm ngưng hoạt động. Vui lòng thử lại sau."

        // Lỗi đăng ký
        lower.contains("already exist") || lower.contains("duplicate") ->
            "Thông tin này đã được đăng ký trước đó."

        lower.contains("name") && lower.contains("required") ->
            "Vui lòng nhập họ và tên."

        lower.contains("address") && lower.contains("required") ->
            "Vui lòng nhập địa chỉ lắp đặt."

        lower.contains("email") && (lower.contains("format") || lower.contains("valid") || lower.contains("correct")) ->
            "Email không hợp lệ. Vui lòng kiểm tra lại."

        // Fallback: nếu là HTTP error code thuần
        lower.startsWith("http ") ->
            "Đã xảy ra lỗi (${message}). Vui lòng thử lại."

        // Fallback cuối cùng: giữ nguyên message gốc nếu không match
        else -> message
    }
}
