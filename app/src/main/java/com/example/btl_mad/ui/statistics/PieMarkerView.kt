package com.example.btl_mad.ui.statistics
import android.content.Context
import android.widget.TextView
import com.example.btl_mad.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.data.Entry
import java.text.DecimalFormat

class PieMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvCategory: TextView = findViewById(R.id.tvCategory)
    private val tvAmount: TextView = findViewById(R.id.tvAmount)
    private val tvPercent: TextView = findViewById(R.id.tvPercent)
    private val formatter = DecimalFormat("#,###")

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e is PieEntry) {
            val category = e.label
            val amount = (e.data as? Int) ?: 0
            val percent = e.value // chính là phần trăm (do bạn đã dùng PieEntry(percent, category))

            tvCategory.text = "Danh mục: $category"
            tvAmount.text = "Số tiền: ${formatter.format(amount)} đ"
            tvPercent.text = "Tỷ lệ: ${"%.1f".format(percent)}%"
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}

