package com.example.btl_mad.data


import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val user_id: Int = 0,
    @SerializedName("dot") val dot: String = "",  // Ngày giao dịch, mặc định chuỗi rỗng
    @SerializedName("in_out_budget") val in_out_budget: String = "chi",  // "chi" hoặc "thu"
    @SerializedName("amount") val amount: Int = 0,
    @SerializedName("transaction_type_name") val transaction_type_name: String = "",
    @SerializedName("note") val note: String = "",
    @SerializedName("transaction_type_id") val transaction_type_id: Int = 0,
    @SerializedName("current_balance") val current_balance: Int = 0,
    @SerializedName("screenshot") val screenshot: String = ""  // Có thể là null (hình ảnh không bắt buộc)
)

