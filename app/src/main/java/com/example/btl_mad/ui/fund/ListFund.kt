package com.example.btl_mad.ui.fund

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.Fund
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class ListFund : AppCompatActivity() {
    private lateinit var textToggleProgress: TextView
    private var isVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fund_list)
        var userId = SharedPrefManager.getUserId(this)
        textToggleProgress = findViewById(R.id.textToggleProgress)

        // Gán sự kiện click để ẩn/hiện các layout có tag = "progressLayout"
        textToggleProgress.setOnClickListener {
            isVisible = !isVisible
            toggleProgressLayouts()
            textToggleProgress.text = if (isVisible) "Ẩn tiến độ" else "Hiện tiến độ"
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFunds)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Giả sử user_id là 5
//        val userId = 5

        // Gọi API để lấy danh sách các quỹ
        getFundsFromAPI(userId)

    }

    private fun toggleProgressLayouts() {
        // Tìm root layout chứa các thẻ và tiến độ
        val parent = findViewById<ViewGroup>(R.id.recyclerViewFunds)
        // Bắt đầu animation chuyển tiếp trong parent
        TransitionManager.beginDelayedTransition(parent, AutoTransition())

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i).findViewWithTag<View>("progressLayout")
            if (child.tag == "progressLayout") {
                child.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun getFundsFromAPI(userId: Int) {
        RetrofitClient.apiService.getTransactionTypes(userId).enqueue(object : Callback<List<Fund>> {
            override fun onResponse(call: Call<List<Fund>>, response: Response<List<Fund>>) {
                if (response.isSuccessful) {
                    val funds = response.body()
                    funds?.let {
                        val adapter = FundAdapter(it)
                        findViewById<RecyclerView>(R.id.recyclerViewFunds).adapter = adapter
                    }
                } else {
                    Toast.makeText(this@ListFund, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Fund>>, t: Throwable) {
                println(t.message)
                Toast.makeText(this@ListFund, "Failed to load funds: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getDetail(view: View) {
        val fundId = view.tag as? Int  // Lấy ID từ tag, bạn đã gán ID cho item trong adapter
        fundId?.let {
            // Xử lý hiển thị chi tiết của quỹ với ID này
            val intent = Intent(this, DetailFundActivity::class.java)

            // Truyền ID qua Intent
            intent.putExtra("FUND_ID", fundId)

            // Start DetailFundActivity
            startActivity(intent)

        }
    }

    fun goNewFund(view: View) {
        // Khi nhấn vào CardView, chuyển đến AddFundActivity
        val intent = Intent(this, AddFundActivity::class.java)
        startActivity(intent)
    }

    fun back(view: View?) {
        finish()
    }
}
