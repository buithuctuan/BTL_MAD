package com.example.btl_mad.data

data class ChangePasswordRequest(
    val id: Int,
    val old_password: String,
    val new_password: String
)