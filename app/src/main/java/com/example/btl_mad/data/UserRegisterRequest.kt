package com.example.btl_mad.data

data class UserRegisterRequest(
    val username: String,
    val pass_: String,
    val full_name: String,
    val dateOfBirth: String,
    val mail: String,
    val phoneNumber: String,
    val sex: String,
    val question_id: Int,
    val answer_for_forgot_password: String,
    val avatar: String? = null
)