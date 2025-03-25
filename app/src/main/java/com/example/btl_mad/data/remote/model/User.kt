package com.example.btl_mad.data.remote.model

data class User(
    val id: String = "",           // ID người dùng (từ Firebase Auth)
    val email: String = "",        // Email
    val firstName: String = "",  // Tên
    val lastName: String = "", //Họ
    val phoneNumber: String = "", //Số đện thoại
    val password: String = "", //Mật khẩu
    val totalBudget: Int = 0, // Ngân sách tổng cho chi tiêu (hoặc thu, tùy thiết kế)
    val budgetPeriod: String = "", // Chu kỳ ngân sách tổng (ví dụ: "monthly")
    val secretQuestion: String = "", // Câu hỏi bí mật (ví dụ: "Tên thú cưng đầu tiên của bạn là gì?")
    val secretAnswer: String = ""  // Câu trả lời bí mật (lưu mã hóa nếu cần bảo mật)
)