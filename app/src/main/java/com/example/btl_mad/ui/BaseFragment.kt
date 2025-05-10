package com.example.btl_mad.ui

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.btl_mad.R
import com.example.btl_mad.ui.main.MainActivity

abstract class BaseFragment : Fragment() {
    abstract fun getLayoutId(): Int
    open fun getToolbarTitle(): String? = null
    open fun useToolbar(): Boolean = true
    open fun showBackButton(): Boolean = true
    // Mặc định không chuyển về home
    open fun shouldNavigateToHomeOnBack(): Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_base, container, false) as ViewGroup
        val titleView = rootView.findViewById<TextView>(R.id.toolbar_title)
        val backBtn = rootView.findViewById<ImageView>(R.id.btn_back)

        // Hiển thị tiêu đề
        titleView?.text = getToolbarTitle()

        // Xử lý nút back
        if (useToolbar()) {
            if (showBackButton()) {
                backBtn?.visibility = View.VISIBLE
                backBtn?.setOnClickListener {
                    if (shouldNavigateToHomeOnBack()) {
                        // Quay về Trang chủ bằng cách gọi BottomNav hoặc Navigation
                        (activity as? MainActivity)?.navigateToHome()
                    } else {
                        requireActivity().onBackPressed()
                    }
                }
            } else {
                backBtn?.visibility = View.GONE
            }
        } else {
            backBtn?.visibility = View.GONE
            titleView?.visibility = View.GONE
        }

        // Thêm nội dung của Fragment con
        val contentView = inflater.inflate(getLayoutId(), rootView, false)
        val containerLayout = rootView.findViewById<ViewGroup>(R.id.fragment_content)
        containerLayout.addView(contentView)

        return rootView
    }
}

