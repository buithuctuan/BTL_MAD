package com.example.btl_mad.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.btl_mad.R

abstract class BaseFragment : Fragment() {
    private var toolbar: Toolbar? = null
    private var toolbarTitle: String? = null

    // Phương thức trừu tượng để lấy layout ID của Fragment
    abstract fun getLayoutId(): Int

    // Phương thức để lấy tiêu đề của Toolbar
    open fun getToolbarTitle(): String? = null

    // Phương thức để thiết lập menu cho Toolbar (nếu cần)
    open fun setupToolbarMenu(toolbar: Toolbar) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Tạo layout chính bao gồm Toolbar và nội dung Fragment
        val rootView = inflater.inflate(R.layout.fragment_base, container, false) as ViewGroup
        toolbar = rootView.findViewById(R.id.toolbar)

        // Thêm layout của Fragment con vào container
        val contentView = inflater.inflate(getLayoutId(), rootView, false)
        val container = rootView.findViewById<ViewGroup>(R.id.fragment_content)
        container.addView(contentView)

        // Thiết lập Toolbar
        toolbar?.let { setupToolbar(it) }

        return rootView
    }

    private fun setupToolbar(toolbar: Toolbar) {
        // Thiết lập tiêu đề
        toolbarTitle = getToolbarTitle()
        toolbar.title = toolbarTitle

        // Thiết lập nút back
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        // Thiết lập menu (nếu có)
        setupToolbarMenu(toolbar)
    }
}