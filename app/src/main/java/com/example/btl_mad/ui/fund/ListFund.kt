package com.example.btl_mad.ui.fund

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.transition.AutoTransition
import android.transition.TransitionManager
import com.example.btl_mad.R

class ListFund : AppCompatActivity() {
    private lateinit var textToggleProgress: TextView
    private var isVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fund_list)

        textToggleProgress = findViewById(R.id.textToggleProgress)

        // Gán sự kiện click để ẩn/hiện các layout có tag = "progressLayout"
        textToggleProgress.setOnClickListener {
            isVisible = !isVisible
            toggleProgressLayouts()
            textToggleProgress.text = if (isVisible) "Ẩn tiến độ" else "Hiện tiến độ"
        }

    }

    private fun toggleProgressLayouts() {
        // Tìm root layout chứa các thẻ và tiến độ
        val parent = findViewById<ViewGroup>(R.id.layoutProgressGroup)
        // Bắt đầu animation chuyển tiếp trong parent
        TransitionManager.beginDelayedTransition(parent, AutoTransition())

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.tag == "progressLayout") {
                child.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    fun goNewFund(view: View) {
        // Khi nhấn vào CardView, chuyển đến AddFundActivity
        val intent = Intent(this, AddFundActivity::class.java)
        startActivity(intent)
    }

    fun goDetailFund(view: View) {
        // Khi nhấn vào CardView, chuyển đến DetailFundActivity
        val intent = Intent(this, DetailFundActivity::class.java)
        startActivity(intent)
    }

    fun back(view: View?) {
        finish()
    }
}
