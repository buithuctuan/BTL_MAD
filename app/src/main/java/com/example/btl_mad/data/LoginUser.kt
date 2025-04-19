package com.example.btl_mad.data

import com.google.gson.annotations.SerializedName

data class LoginUser(
    @SerializedName("username") val username: String,
    @SerializedName("pass_") val pass_: String
)