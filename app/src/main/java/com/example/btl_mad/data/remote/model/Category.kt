package com.example.btl_mad.data.remote.model

data class Category(
    val id: String = "",         // ID duy nhất (do Firebase tạo)
    val name: String = "",       // Tên danh mục (ví dụ: "Ăn uống")
    val type: String = "",       // Loại: "income" hoặc "expense"
    val budget: Int = 0,    // Ngân sách tối đa cho danh mục này
    val period: String = ""      // Chu kỳ ngân sách (ví dụ: "monthly", "weekly")
)