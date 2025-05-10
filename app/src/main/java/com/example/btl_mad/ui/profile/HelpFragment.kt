package com.example.btl_mad.ui.profile

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.btl_mad.R
import com.example.btl_mad.ui.BaseFragment

class HelpFragment : BaseFragment() {
    override fun getLayoutId() = R.layout.fragment_help
    override fun getToolbarTitle(): String? = "Trợ giúp"
    override fun useToolbar() = true
    override fun showBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listView = view.findViewById<ExpandableListView>(R.id.faqListView)
        val feedbackBtn = view.findViewById<Button>(R.id.btnSendFeedback)

        val headers = listOf(
            "Làm sao để thêm giao dịch?",
            "Làm sao để đặt hạn mức chi tiêu?",
            "Tôi quên mật khẩu thì làm sao?",
        )

        val answers = mapOf(
            headers[0] to listOf("Bạn vào tab Trang chủ → Nhấn nút ➕ → Chọn hũ và nhập thông tin."),
            headers[1] to listOf("Vào phần Thống kê → Chọn hũ → Nhấn 'Sửa' để đặt hạn mức."),
            headers[2] to listOf("Vào màn hình đăng nhập → chọn 'Quên mật khẩu' và làm theo hướng dẫn.")
        )

        val adapter = object : BaseExpandableListAdapter() {
            override fun getGroupCount() = headers.size
            override fun getChildrenCount(groupPosition: Int) = answers[headers[groupPosition]]!!.size
            override fun getGroup(groupPosition: Int) = headers[groupPosition]
            override fun getChild(groupPosition: Int, childPosition: Int) = answers[headers[groupPosition]]!![childPosition]
            override fun getGroupId(groupPosition: Int) = groupPosition.toLong()
            override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()
            override fun hasStableIds() = false
            override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

            override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
                val textView = TextView(context).apply {
                    text = getGroup(groupPosition)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTypeface(null, Typeface.BOLD)
                    setPadding(32, 24, 16, 24)
                }
                return textView
            }

            override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
                val textView = TextView(context).apply {
                    text = getChild(groupPosition, childPosition)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    setPadding(64, 16, 16, 16)
                }
                return textView
            }
        }

        listView.setAdapter(adapter)

        feedbackBtn.setOnClickListener {
            val url = "https://docs.google.com/forms/d/e/1FAIpQLSe8uNHsJNHTlttmHmSHdgWYUVeHXllJTHqA59RdqXQdbLj7MQ/viewform?usp=dialog"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}
