package com.example.btl_mad.data.remote.model

data class Transaction(
    val id: String = "",         // ID duy nhất (do Firebase tạo)
    val amount: Int = 0,    // Số tiền
    val date: String = "",       // Ngày giao dịch (dạng "yyyy-MM-dd")
    val categoryId: String = "", // ID của danh mục (liên kết với Category)
    val type: String = "",       // Loại: "income" (thu) hoặc "expense" (chi)
    val note: String = ""        // Ghi chú (tùy chọn)
)