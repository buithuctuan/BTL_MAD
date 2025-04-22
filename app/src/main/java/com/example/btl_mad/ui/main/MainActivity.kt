package com.example.btl_mad.ui.main

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.ui.home.HomeFragment
import com.example.btl_mad.ui.statistics.StatisticsFragment
import com.example.btl_mad.ui.BaseFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navTransactions: LinearLayout
    private lateinit var navStatistics: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        // Gắn các nav
        navHome = findViewById(R.id.nav_home)
        navTransactions = findViewById(R.id.nav_transactions)
        navStatistics = findViewById(R.id.nav_statistics)
        navProfile = findViewById(R.id.nav_profile)

        // Hiển thị HomeFragment mặc định
        navigateToHome()

        // Gắn sự kiện click
        navHome.setOnClickListener {
            navigateToHome()
        }

        navStatistics.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, StatisticsFragment())
                .commit()
            setActiveTab(navStatistics)
        }

        // Mở rộng nếu bạn có TransactionFragment hoặc ProfileFragment sau này
    }

    fun navigateToHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, HomeFragment())
            .commit()
        setActiveTab(navHome)
    }

    private fun setActiveTab(selectedNav: LinearLayout) {
        val allNavs = listOf(navHome, navTransactions, navStatistics, navProfile)
        allNavs.forEach { it.isSelected = false }
        selectedNav.isSelected = true
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)

        // Nếu fragment hiện tại là 1 BaseFragment và yêu cầu quay lại Home
        if (currentFragment is BaseFragment && currentFragment.shouldNavigateToHomeOnBack()) {
            navigateToHome()
        } else {
            super.onBackPressed()
        }
    }
}
