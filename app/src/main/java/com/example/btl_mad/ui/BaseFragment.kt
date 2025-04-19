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

    abstract fun getLayoutId(): Int
    open fun getToolbarTitle(): String? = null
    open fun setupToolbarMenu(toolbar: Toolbar) {}
    open fun useToolbar(): Boolean = true // Cho phép tắt toolbar với một số Fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_base, container, false) as ViewGroup
        toolbar = rootView.findViewById(R.id.toolbar)

        if (!useToolbar()) {
            toolbar?.visibility = View.GONE
        } else {
            setupToolbar(toolbar!!)
        }

        val contentView = inflater.inflate(getLayoutId(), rootView, false)
        val containerLayout = rootView.findViewById<ViewGroup>(R.id.fragment_content)
        containerLayout.addView(contentView)

        return rootView
    }

    private fun setupToolbar(toolbar: Toolbar) {
        toolbar.title = getToolbarTitle()
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        setupToolbarMenu(toolbar)
    }
}

