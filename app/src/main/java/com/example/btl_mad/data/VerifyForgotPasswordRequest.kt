package com.example.btl_mad.data

import com.google.gson.annotations.SerializedName

data class VerifyForgotPasswordRequest(
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("question_id") val question_id: Int,
    @SerializedName("answer_for_forgot_password") val answer_for_forgot_password: String
)