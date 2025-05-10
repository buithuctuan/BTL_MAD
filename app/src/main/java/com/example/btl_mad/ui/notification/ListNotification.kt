package com.example.btl_mad.ui.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.Notification
import com.example.btl_mad.ui.main.MainActivity
import com.example.btl_mad.ui.utils.SharedPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListNotification : AppCompatActivity() {
    private var userId: Int = -1
    private lateinit var navHome: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications) // Layout chứa RecyclerView

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Gọi API để lấy danh sách thông báo
        userId = SharedPrefManager.getUserId(this)

        getNotificationsFromAPI(userId) // Gọi API với user_id là 5

        navHome = findViewById(R.id.nav_home)
        navHome.setOnClickListener {
            navigateToHome()
        }
    }

    fun navigateToHome() {
        val intent = Intent(this@ListNotification, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getNotificationsFromAPI(userId: Int) {
        RetrofitClient.apiService.getNotifications(userId).enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful) {
                    val notifications = response.body()
                    notifications?.let {
                        // Cập nhật RecyclerView với dữ liệu từ API
                        println(it)
                        val adapter = NotificationAdapter(it)
                        findViewById<RecyclerView>(R.id.recyclerViewNotifications).adapter = adapter
                    }
                } else {
                    Toast.makeText(this@ListNotification, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                Toast.makeText(this@ListNotification, "Failed to load notifications: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun back(view: View) {
        finish()
    }
}
