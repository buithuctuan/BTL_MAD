package com.example.btl_mad.ui.fund

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.GridView
import com.example.btl_mad.R


import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var iconSelector: LinearLayout
    private lateinit var selectedIcon: ImageView

    private val iconList = listOf(
        R.drawable.fund_icon_1,
        R.drawable.fund_icon_2,
        R.drawable.fund_icon_3,
        R.drawable.fund_icon_4,
        R.drawable.fund_icon_5,
        R.drawable.fund_icon_6,
        R.drawable.fund_icon_6,
        R.drawable.fund_icon_6
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho BottomSheet
        val view = inflater.inflate(R.layout.dialog_edit, container, false)


        iconSelector = view.findViewById(R.id.iconSelector)
        selectedIcon = view.findViewById(R.id.selectedIcon)

        iconSelector.setOnClickListener {
            showIconDialog()
        }

        // Tìm nút đóng và gán sự kiện
        val closeButton = view.findViewById<ImageView>(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss() // Đóng BottomSheet khi nhấn nút "X"
        }

        return view
    }
    private fun showIconDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_icon_grid, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gridViewIcons)

        val adapter = object : BaseAdapter() {
            override fun getCount() = iconList.size
            override fun getItem(position: Int) = iconList[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_icon, parent, false)
                val image = view.findViewById<ImageView>(R.id.icon)
                image.setImageResource(iconList[position])
                return view
            }
        }

        gridView.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        gridView.setOnItemClickListener { _, _, position, _ ->
            selectedIcon.setImageResource(iconList[position])
            dialog.dismiss()
        }

        dialog.show()
    }

}

