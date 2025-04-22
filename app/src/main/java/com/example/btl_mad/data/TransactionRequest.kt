package com.example.btl_mad.data

data class TransactionRequest(
    val user_id: Int,                      // user_id
    val in_out_budget: String? = null,       // in_out_budget (Optional)
    val time_range: String? = null,         // time_range (Optional)
    val transaction_type_ids: List<Int> = listOf() // transaction_type_ids (Optional, default empty list)
)

