package com.example.btl_mad.data

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("newPassword") val newPassword: String
)