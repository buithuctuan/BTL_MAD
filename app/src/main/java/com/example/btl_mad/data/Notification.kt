package com.example.btl_mad.data

data class Notification(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val date: String
)