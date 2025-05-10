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

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return prefs.getString("username", "Người dùng") ?: "Người dùng"
    }

    fun clearAll(context: Context) {
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun getUserProfile(context: Context): JSONObject? {
        val shared = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userJson = shared.getString("user", null)
        return userJson?.let { JSONObject(it) }
    }

    fun updateUserProfile(context: Context, updated: JSONObject) {
        val shared = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit()
        shared.putString("user", updated.toString())
        shared.apply()
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getBoolean("notification_enabled", true)
    }

    fun setNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notification_enabled", enabled).apply()
    }


}
