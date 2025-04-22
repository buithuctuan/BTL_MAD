package com.example.btl_mad.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("full_name") val full_name: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String, // ISO 8601 format, e.g., "2023-01-01"
    @SerializedName("mail") val mail: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("sex") val sex: String,
    @SerializedName("avatar") val avatar: String? = null
)