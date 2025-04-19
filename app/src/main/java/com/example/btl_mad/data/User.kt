package com.example.btl_mad.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("username") val username: String,
    @SerializedName("pass_") val pass_: String,
    @SerializedName("full_name") val full_name: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String, // ISO 8601 format, e.g., "2023-01-01"
    @SerializedName("mail") val mail: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("sex") val sex: String,
    @SerializedName("question_id") val question_id: Int,
    @SerializedName("answer_for_forgot_password") val answer_for_forgot_password: String,
    @SerializedName("avatar") val avatar: String? = null
)