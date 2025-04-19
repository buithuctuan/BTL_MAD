package com.example.btl_mad.ui.main

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.btl_mad.R
import com.example.btl_mad.ui.home.HomeFragment
//import com.example.btl_mad.ui.StatisticsFragment
//import com.example.btl_mad.ui.TransactionsFragment
//import com.example.btl_mad.ui.ProfileFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        // Gắn fragment mặc định
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        setupBottomNav()
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            loadFragment(HomeFragment())
        }

//        findViewById<LinearLayout>(R.id.nav_transactions).setOnClickListener {
//            loadFragment(TransactionsFragment())
//        }
//
//        findViewById<LinearLayout>(R.id.nav_add).setOnClickListener {
//            // Mở màn thêm giao dịch hoặc dialog
//        }
//
//        findViewById<LinearLayout>(R.id.nav_statistics).setOnClickListener {
//            loadFragment(StatisticsFragment())
//        }
//
//        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
//            loadFragment(ProfileFragment())
//        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }
}
