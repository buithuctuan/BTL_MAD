package com.example.btl_mad.data

data class Transaction(
    val id: Int = 0,
    val user_id: Int = 0,
    val dot: String = "",  // Ngày giao dịch, mặc định chuỗi rỗng
    val in_out_budget: String = "chi",  // "chi" hoặc "thu"
    val amount: Int = 0,
    val transaction_type_name: String = "",
    val note: String = "",
    val transaction_type_id: Int = 0,
    val current_balance: Int = 0,
    val screenshot: String = ""  // Có thể là null (hình ảnh không bắt buộc)
)