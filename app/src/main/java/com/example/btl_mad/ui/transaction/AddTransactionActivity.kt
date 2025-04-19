package com.example.btl_mad.ui.transaction

import android.content.Intent
import android.net.Uri
import java.io.File
import androidx.core.content.FileProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.btl_mad.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var datePickerLayout: LinearLayout
    private lateinit var datePickerText: TextView
    private lateinit var amountInput: EditText
    private lateinit var categoryLayout: LinearLayout
    private lateinit var categoryText: TextView
    private lateinit var noteInput: EditText
    private lateinit var saveButton: Button
    private lateinit var iconImage: ImageView
    private lateinit var toolbar: Toolbar
    private var cameraImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { iconImage.setImageURI(it) }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            cameraImageUri?.let {
                iconImage.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_transactions)

        // Ánh xạ các thành phần giao diện
        toolbar = findViewById(R.id.toolbar)
        datePickerLayout = findViewById(R.id.datePickerLayout)
        datePickerText = findViewById(R.id.datePickerText)
        amountInput = findViewById(R.id.amountInput)
        categoryLayout = findViewById(R.id.categoryLayout)
        categoryText = findViewById(R.id.categoryText)
        noteInput = findViewById(R.id.noteInput)
        saveButton = findViewById(R.id.saveButton)
        iconImage = findViewById(R.id.iconImage)

        // Thiết lập Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Thiết lập DatePicker cho toàn bộ LinearLayout
        datePickerLayout.setOnClickListener {
            showCustomDatePickerDialog()
        }

        // Đảm bảo amountInput hiển thị bàn phím số
        amountInput.setOnClickListener {
            amountInput.isCursorVisible = true
        }

        // Xử lý icon ảnh
        iconImage.setOnClickListener {
            showImagePickerDialog()
        }

        // Xử lý phân loại
        categoryLayout.setOnClickListener {
            showCategoryPickerDialog()
        }

        // Xử lý nút Lưu
        saveButton.setOnClickListener {
            val date = datePickerText.text.toString()
            val amount = amountInput.text.toString()
            val category = categoryText.text.toString()
            val note = noteInput.text.toString()

            if (date == "Chọn ngày" || amount.isEmpty() || category == "Lựa chọn phân loại") {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            } else {
                showSuccessDialog()
            }
        }
    }

    private fun showCustomDatePickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_date_picker_dialog, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val monthYearText: TextView = dialogView.findViewById(R.id.monthYearText)
        val prevMonth: ImageView = dialogView.findViewById(R.id.prevMonth)
        val nextMonth: ImageView = dialogView.findViewById(R.id.nextMonth)
        val calendarGrid: GridView = dialogView.findViewById(R.id.calendarGrid)

        val calendar = Calendar.getInstance()
        var selectedYear = calendar.get(Calendar.YEAR)
        var selectedMonth = calendar.get(Calendar.MONTH)
        var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Định dạng tháng và năm
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("vi"))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Cập nhật tiêu đề tháng và năm
        calendar.set(selectedYear, selectedMonth, 1)
        monthYearText.text = "Tháng ${selectedMonth + 1} $selectedYear"

        // Tạo danh sách ngày trong tháng
        fun updateCalendar() {
            calendar.set(selectedYear, selectedMonth, 1)
            val firstDayOfMonth = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Điều chỉnh để Th2 là ngày đầu tiên
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val days = mutableListOf<Int>()
            // Thêm các ô trống trước ngày đầu tiên của tháng
            for (i in 0 until firstDayOfMonth) {
                days.add(0)
            }
            // Thêm các ngày trong tháng
            for (i in 1..daysInMonth) {
                days.add(i)
            }

            val datePickerText = findViewById<TextView>(R.id.datePickerText)

            // Cập nhật GridView
            val adapter = CalendarAdapter(this, days, selectedDay, selectedMonth + 1, selectedYear) { day ->
            selectedDay = day

                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth, day)

                val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN"))
                val formattedDate = sdf.format(calendar.time)

                datePickerText.text = formattedDate.replaceFirstChar { it.uppercase() } // Viết hoa chữ cái đầu
            }
            calendarGrid.adapter = adapter
        }

        // Cập nhật lịch ban đầu
        updateCalendar()

        // Xử lý nút Previous Month
        prevMonth.setOnClickListener {
            selectedMonth--
            if (selectedMonth < 0) {
                selectedMonth = 11
                selectedYear--
            }
            calendar.set(selectedYear, selectedMonth, 1)
            monthYearText.text = "Tháng ${selectedMonth + 1} $selectedYear"
            updateCalendar()
        }

        // Xử lý nút Next Month
        nextMonth.setOnClickListener {
            selectedMonth++
            if (selectedMonth > 11) {
                selectedMonth = 0
                selectedYear++
            }
            calendar.set(selectedYear, selectedMonth, 1)
            monthYearText.text = "Tháng ${selectedMonth + 1} $selectedYear"
            updateCalendar()
        }

        // Xử lý khi chọn ngày
        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            Log.d("CalendarDebug", "Item clicked at position: $position")
            val day = (calendarGrid.adapter as CalendarAdapter).getItem(position) as Int
            Log.d("CalendarDebug", "Selected day: $day")
            if (day != 0) {
                selectedDay = day
                calendar.set(selectedYear, selectedMonth, selectedDay)
                datePickerText.text = dateFormat.format(calendar.time)
                updateCalendar() // Cập nhật lại lịch để làm nổi bật ngày được chọn
                dialog.dismiss()
            }
        }

        // Hiển thị dialog ở góc dưới màn hình
        dialog.show()
        dialog.window?.let { window ->
            val params = window.attributes
            params.gravity = Gravity.BOTTOM
            params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Chọn ảnh từ thư viện", "Chụp ảnh mới")
        MaterialAlertDialogBuilder(this)
            .setTitle("Chọn ảnh")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageLauncher.launch("image/*")
                    1 -> {
                        val imageFile = File.createTempFile("camera_", ".jpg", externalCacheDir)
                        cameraImageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)

                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                        takePictureLauncher.launch(takePictureIntent)
                    }
                }
            }
            .show()
    }

    private fun showCategoryPickerDialog() {
        val categories = resources.getStringArray(R.array.expense_categories)
        MaterialAlertDialogBuilder(this)
            .setTitle("Chọn phân loại")
            .setItems(categories) { _, which ->
                categoryText.text = categories[which]
            }
            .show()
    }

    private fun showSuccessDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.success_dialog, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnBack: Button = dialogView.findViewById(R.id.btnBack)
        val btnCreateNew: Button = dialogView.findViewById(R.id.btnCreateNew)

        btnBack.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        btnCreateNew.setOnClickListener {
            dialog.dismiss()
            datePickerText.text = "Chọn ngày"
            amountInput.setText("")
            categoryText.text = "Lựa chọn phân loại"
            noteInput.setText("")
            iconImage.setImageResource(R.drawable.ic_anh_hien_thi)
        }

        dialog.show()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}