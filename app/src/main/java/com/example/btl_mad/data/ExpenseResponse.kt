package com.example.btl_mad.data

data class ExpenseResponse(
    val success: Boolean,
    val message: String,
    val current_balance: Int? // Số dư hiện tại sau giao dịch
)