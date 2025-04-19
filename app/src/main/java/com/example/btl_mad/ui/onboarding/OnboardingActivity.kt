package com.example.btl_mad.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.btl_mad.R

import com.example.btl_mad.data.remote.model.Transaction
import com.example.btl_mad.ui.login.LoginActivity


import com.example.btl_mad.ui.main.MainActivity
import com.example.btl_mad.ui.transaction.AddTransactionActivity

// OnboardingActivity: Màn hình giới thiệu với 2 trang, cho phép người dùng xem qua hoặc bỏ qua để vào MainActivity.
class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsContainer: LinearLayout
    private lateinit var ibNext: ImageButton
    private lateinit var tvSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Khởi tạo các view
        viewPager = findViewById(R.id.viewPager)
        dotsContainer = findViewById(R.id.dotsContainer)
        ibNext = findViewById(R.id.ibNext)
        tvSkip = findViewById(R.id.tvSkip)

        // Thiết lập ViewPager2
        val adapter = OnboardingAdapter()
        viewPager.adapter = adapter

        // Tạo chấm tròn
        setupDots(adapter.itemCount)

        // Cập nhật chấm tròn khi chuyển trang
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
            }
        })

        // Xử lý nút "Skip"
        tvSkip.setOnClickListener {
            android.util.Log.d("OnboardingActivity", "Skip clicked, navigating to LoginActivity")
            // Lưu trạng thái đã xem onboarding
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("hasSeenOnboarding", true).apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Xử lý nút "Tiếp theo"
        ibNext.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < adapter.itemCount - 1) {
                android.util.Log.d(
                    "OnboardingActivity",
                    "Next clicked, moving to page ${currentItem + 1}"
                )
                viewPager.currentItem = currentItem + 1
            } else {

                android.util.Log.d(
                    "OnboardingActivity",
                    "Next clicked on last page, navigating to LoginActivity"
                )
                // Lưu trạng thái đã xem onboarding
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("hasSeenOnboarding", true).apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupDots(count: Int) {
        dotsContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx()).apply {
                    setMargins(4.dpToPx(), 0, 4.dpToPx(), 0)
                }
                setBackgroundResource(if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive)
                id = View.generateViewId()
            }
            dotsContainer.addView(dot)
        }
    }

    private fun updateDots(position: Int) {
        for (i in 0 until dotsContainer.childCount) {
            dotsContainer.getChildAt(i).setBackgroundResource(
                if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
        // Ẩn "Skip" ở trang cuối
        tvSkip.visibility = if (position == dotsContainer.childCount - 1) View.GONE else View.VISIBLE
    }

    // Hàm tiện ích chuyển dp sang px
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}