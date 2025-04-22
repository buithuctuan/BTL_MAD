import android.content.Context
import android.util.Log
import android.widget.TextView
import com.example.btl_mad.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.MPPointF
import java.text.DecimalFormat

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvLabel: TextView = findViewById(R.id.tvLabel)
    private val tvValue1: TextView = findViewById(R.id.tvValue1)
    private val tvValue2: TextView = findViewById(R.id.tvValue2)
    private val formatter = DecimalFormat("#,###")

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null || chartView == null) return

        val x = e.x
        val lineData = (chartView?.data ?: return) as LineData
        val dataSets = lineData.dataSets
        Log.d("MARKER_DEBUG", "refreshContent called - Entry x=$x, y=${e.y}")
        Log.d("MARKER_DEBUG", "DataSets count: ${dataSets.size}")

        tvLabel.text = "Ngày ${x.toInt() + 1}"

        if (dataSets.size >= 2) {
            val dataSet1 = dataSets[0] as LineDataSet
            val dataSet2 = dataSets[1] as LineDataSet

            val entry1 = dataSet1.getEntryForXValue(x, Float.NaN)
            val entry2 = dataSet2.getEntryForXValue(x, Float.NaN)

            val value1 = entry1?.y ?: 0f
            val value2 = entry2?.y ?: 0f

            tvValue1.text = "${dataSet1.label}: ${formatter.format(value1)} đ"
            tvValue2.text = "${dataSet2.label}: ${formatter.format(value2)} đ"
        } else {
            tvValue1.text = ""
            tvValue2.text = ""
        }

        super.refreshContent(e, highlight)
    }


    override fun getOffset(): MPPointF {
        val offset = MPPointF(-(width / 2).toFloat(), -height.toFloat())
        Log.d("MARKER_DEBUG", "Offset: $offset")
        return offset
    }
}
