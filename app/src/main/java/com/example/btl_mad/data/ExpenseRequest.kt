package com.example.btl_mad.data

data class ExpenseRequest(
    val user_id: Int,
    val dot: String, // Định dạng "dd/MM/yyyy"
    val in_out_budget: String = "chi", // Luôn là "chi" cho chi tiêu
    val amount: Double,
    val transaction_type_id: Int,
    val note: String,
    val screenshot: String? // Đường dẫn ảnh, có thể null nếu không có
)