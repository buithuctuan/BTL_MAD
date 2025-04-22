package com.example.btl_mad.utils

import android.content.Context
import org.json.JSONObject

object SharedPrefManager {
    fun getUserId(context: Context): Int {
        val shared = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userJson = shared.getString("user", null)
        return if (userJson != null) {
            try {
                val json = JSONObject(userJson)
                json.getInt("id")
            } catch (e: Exception) {
                e.printStackTrace()
                -1
            }
        } else {
            -1
        }
    }
}
