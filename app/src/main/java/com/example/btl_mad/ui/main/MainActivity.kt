package com.example.btl_mad.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.btl_mad.R
import com.example.btl_mad.ui.home.HomeFragment
import com.example.btl_mad.ui.statistics.StatisticsFragment
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.ui.fund.AddFundActivity
import com.example.btl_mad.ui.profile.ProfileFragment
import com.example.btl_mad.ui.transaction.AddTransactionExpenseActivity
import com.example.btl_mad.ui.transaction.AddTransactionIncomeActivity
import com.example.btl_mad.ui.transactionhistory.SpendingHistory
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navTransactions: LinearLayout
    private lateinit var navStatistics: LinearLayout
    private lateinit var navProfile: LinearLayout
    private  lateinit var nav_add: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        // Gắn các nav
        navHome = findViewById(R.id.nav_home)
        navTransactions = findViewById(R.id.nav_transactions)
        navStatistics = findViewById(R.id.nav_statistics)
        navProfile = findViewById(R.id.nav_profile)
        nav_add = findViewById(R.id.nav_add)

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

        //Lắng nghe sự kiện click thu chi
        navTransactions.setOnClickListener{
            val intent = Intent(this, SpendingHistory::class.java)
            startActivity(intent)
        }
        // Lắng nghe sk click dâu +
        nav_add.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.dialog_choose_type, null)

            dialog.setContentView(view)
            dialog.show()

            val btnIncome = view.findViewById<TextView>(R.id.btnIncome)
            val btnExpense = view.findViewById<TextView>(R.id.btnExpense)
            val btnCancel = view.findViewById<TextView>(R.id.btnCancel)

            btnIncome.setOnClickListener {
                startActivity(Intent(this, AddTransactionIncomeActivity::class.java))
                dialog.dismiss()
            }

            btnExpense.setOnClickListener {
                startActivity(Intent(this, AddTransactionExpenseActivity::class.java))
                dialog.dismiss()
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }


        // Mở rộng nếu bạn có TransactionFragment hoặc ProfileFragment sau này
        navProfile.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, ProfileFragment())
                .commit()
            setActiveTab(navProfile)
        }
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
