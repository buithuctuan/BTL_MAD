package com.example.btl_mad.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.btl_mad.R

abstract class BaseFragment : Fragment() {
    abstract fun getLayoutId(): Int
    open fun getToolbarTitle(): String? = null
    open fun useToolbar(): Boolean = true
    open fun showBackButton(): Boolean = true
    open fun shouldNavigateToHomeOnBack(): Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("BASE_FRAGMENT", "onCreateView called for ${this::class.java.simpleName}")

        val rootView = inflater.inflate(R.layout.fragment_base, container, false) as ViewGroup
        val titleView = rootView.findViewById<TextView>(R.id.toolbar_title)
        val backBtn = rootView.findViewById<ImageView>(R.id.btn_back)

        titleView?.text = getToolbarTitle()
        Log.d("BASE_FRAGMENT", "Title: ${getToolbarTitle()}")

        if (useToolbar()) {
            titleView?.visibility = View.VISIBLE
            if (showBackButton()) {
                backBtn?.visibility = View.VISIBLE
                backBtn?.setOnClickListener {
                    Log.d("BASE_FRAGMENT", "Back button clicked")
                    if (shouldNavigateToHomeOnBack()) {
                        Log.d("BASE_FRAGMENT", "Back action: popBackStackImmediate")
                        requireActivity().supportFragmentManager.popBackStackImmediate()
                    } else {
                        Log.d("BASE_FRAGMENT", "Back action: onBackPressedDispatcher")
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            } else {
                backBtn?.visibility = View.GONE
                Log.d("BASE_FRAGMENT", "Back button hidden")
            }
        } else {
            titleView?.visibility = View.GONE
            backBtn?.visibility = View.GONE
            Log.d("BASE_FRAGMENT", "Toolbar hidden")
        }

        val contentView = inflater.inflate(getLayoutId(), rootView, false)
        val containerLayout = rootView.findViewById<ViewGroup>(R.id.fragment_content)
        containerLayout.removeAllViews()
        containerLayout.addView(contentView)

        Log.d("BASE_FRAGMENT", "Content layout added for ${this::class.java.simpleName}")

        return rootView
    }
}
