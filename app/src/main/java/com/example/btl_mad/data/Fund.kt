package com.example.btl_mad.data

data class Fund(
    val id: Int,
    val name: String,
    val icon: String,
    val user_id: Int,
    val date_create: String,
    val budget: Int,
    val total_expenses: Int,
    val days_left: Int,
    val spending_limit: Int
)