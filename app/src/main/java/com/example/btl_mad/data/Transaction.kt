package com.example.btl_mad.data

data class Transaction(
    val id: Int,
    val user_id: Int,
    val dot: String,
    val in_out_budget: String,
    val color_code: String,
    val amount: Int,
    val transaction_type_name: String,
    val transaction_type_id: Int,
    val note: String,
    val current_balance: Int,
    val screenshot: String?
)