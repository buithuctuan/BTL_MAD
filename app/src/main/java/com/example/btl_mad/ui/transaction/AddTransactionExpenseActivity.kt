package com.example.btl_mad.ui.transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.ExpenseRequest
import com.example.btl_mad.data.FundInfo
import com.example.btl_mad.data.TransactionType
import com.example.btl_mad.data.User
import com.example.btl_mad.ui.fund.AddFundActivity
import com.example.btl_mad.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.appcompat.app.AlertDialog
import com.example.btl_mad.data.FundResponse

class AddTransactionExpenseActivity : AppCompatActivity() {

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
    private var transactionTypes: List<TransactionType> = emptyList()
    private var transaction_type_id = -1
    private var user_id = -1

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { iconImage.setImageURI(it) }
        cameraImageUri = uri
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
        setContentView(R.layout.add_transactions_expense)

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
            if (transactionTypes.isEmpty()) {
                fetchTransactionTypes()
            } else {
                showCategoryPickerDialog()
            }
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
                saveExpenseToServer(date, amount, category, note)
            }
        }

        // Lấy danh sách transaction_type khi khởi động
        fetchTransactionTypes()
    }
    override fun onResume() {
        super.onResume()
        fetchTransactionTypes()
    }



    private fun fetchTransactionTypes() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userJson = sharedPreferences.getString("user", null)
        val user = Gson().fromJson(userJson, User::class.java)

        RetrofitClient.apiService.getTransactionTypesByQuery(user.id).enqueue(object : Callback<List<TransactionType>> {
            override fun onResponse(call: Call<List<TransactionType>>, response: Response<List<TransactionType>>) {
                if (response.isSuccessful) {
                    transactionTypes = response.body() ?: emptyList()
                    if (transactionTypes.isEmpty()) {
                        Toast.makeText(this@AddTransactionExpenseActivity, "Không có phân loại nào!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AddTransactionExpenseActivity, "Lỗi khi lấy danh sách phân loại: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TransactionType>>, t: Throwable) {
                Toast.makeText(this@AddTransactionExpenseActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveExpenseToServer(date: String, amount: String, category: String, note: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userJson = sharedPreferences.getString("user", null)

        val userId = if (userJson != null) {
            try {
                val user = Gson().fromJson(userJson, User::class.java)
                user.id // Đây là Int, không phải Double
            } catch (e: Exception) {
                Log.e("ERROR", "Lỗi khi parse userJson: ${e.message}")
                0
            }
        } else {
            0
        }

        // Nếu không lấy được user_id thì không gửi request
        if (userId == 0) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
            return
        }
        user_id = userId
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        val transactionTypeId = getTransactionTypeIdFromCategory(category)
        transaction_type_id = transactionTypeId
        val screenshotPath = cameraImageUri?.let { uri ->
            uri.toString()
        }

        val formattedDate = if (date.contains(",")) {
            date.split(", ")[1]
        } else {
            date
        }

        val request = ExpenseRequest(
            user_id = userId,
            dot = formattedDate,
            amount = amountValue,
            transaction_type_id = transactionTypeId,
            note = note,
            screenshot = screenshotPath
        )

        RetrofitClient.apiService.saveExpense(request).enqueue(object : Callback<com.example.btl_mad.data.ExpenseResponse> {
            override fun onResponse(call: Call<com.example.btl_mad.data.ExpenseResponse>, response: Response<com.example.btl_mad.data.ExpenseResponse>) {
                if (response.isSuccessful) {
                    val expenseResponse = response.body()
                    if (expenseResponse?.success == true) {
                        Toast.makeText(this@AddTransactionExpenseActivity, "Lưu giao dịch thành công!", Toast.LENGTH_SHORT).show()
                        checkIfOverBudget()
                        showSuccessDialog()
                    } else {
                        Toast.makeText(this@AddTransactionExpenseActivity, "Lưu giao dịch thất bại: ${expenseResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AddTransactionExpenseActivity, "Lỗi server: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<com.example.btl_mad.data.ExpenseResponse>, t: Throwable) {
                Toast.makeText(this@AddTransactionExpenseActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIfOverBudget(){
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        RetrofitClient.apiService.getFundInfo(transaction_type_id, user_id, month, year).enqueue(object : Callback<List<FundInfo>> {
            override fun onResponse(call: Call<List<FundInfo>>, response: Response<List<FundInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    val fundInfoList = response.body() // Lấy danh sách các FundInfo

                    // Kiểm tra danh sách không rỗng
                    if (fundInfoList.isNullOrEmpty()) {
                        Toast.makeText(this@AddTransactionExpenseActivity, "Hũ này chưa có giao dịch", Toast.LENGTH_SHORT).show()
                    } else {
                        val fundInfo = fundInfoList.firstOrNull()

                        if (fundInfo != null) {
                            val total_spent = fundInfo.total_spent
                            val budget = fundInfo.budget
                            if(total_spent > budget){
                                var over = total_spent - budget
                                var fundName = fundInfo.name
                                var title = "Vượt quá mức chi tiêu"
                                var content =  "Hũ chi tiêu \"$fundName\" tháng này đã vượt quá $over VNĐ"
                                addNotification(title, content, fundName, over)
                            }

                        } else {
                            Toast.makeText(this@AddTransactionExpenseActivity, "Hũ này chưa có giao dịch", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this@AddTransactionExpenseActivity, "Lỗi API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FundInfo>>, t: Throwable) {
                Toast.makeText(this@AddTransactionExpenseActivity, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addNotification(noti_title: String, noti_content: String, fund_name: String, over: Int){
        RetrofitClient.apiService.addNotification(user_id, noti_title, noti_content).enqueue(object : Callback<FundResponse> {
            override fun onResponse(call: Call<FundResponse>, response: Response<FundResponse>) {
                if (response.isSuccessful) {
                    val fundResponse = response.body()
                    if (fundResponse != null) {
                        if (fundResponse.status == 200) {
                            showAlert(fund_name, over)
                        } else {
                            Toast.makeText(this@AddTransactionExpenseActivity, fundResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@AddTransactionExpenseActivity, "Có lỗi khi thêm thông báo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FundResponse>, t: Throwable) {
                Toast.makeText(this@AddTransactionExpenseActivity, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showAlert(name: String, money: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_over_budget_dialog, null)

        val messageTextView = dialogView.findViewById<TextView>(R.id.message)
        messageTextView.text = "Hũ chi tiêu \"$name\" tháng này đã vượt quá $money VNĐ"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.btnCreateNew).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getTransactionTypeIdFromCategory(category: String): Int {
        return transactionTypes.find { it.name == category }?.id ?: 0
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

        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("vi"))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        calendar.set(selectedYear, selectedMonth, 1)
        monthYearText.text = "Tháng ${selectedMonth + 1} $selectedYear"

        fun updateCalendar() {
            calendar.set(selectedYear, selectedMonth, 1)
            val firstDayOfMonth = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val days = mutableListOf<Int>()
            for (i in 0 until firstDayOfMonth) {
                days.add(0)
            }
            for (i in 1..daysInMonth) {
                days.add(i)
            }

            val datePickerText = findViewById<TextView>(R.id.datePickerText)

            val adapter = CalendarAdapter(this, days, selectedDay, selectedMonth + 1, selectedYear) { day ->
                selectedDay = day

                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth, day)

                val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN"))
                val formattedDate = sdf.format(calendar.time)

                datePickerText.text = formattedDate.replaceFirstChar { it.uppercase() }
            }
            calendarGrid.adapter = adapter
        }

        updateCalendar()

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

        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            Log.d("CalendarDebug", "Item clicked at position: $position")
            val day = (calendarGrid.adapter as CalendarAdapter).getItem(position) as Int
            Log.d("CalendarDebug", "Selected day: $day")
            if (day != 0) {
                selectedDay = day
                calendar.set(selectedYear, selectedMonth, selectedDay)
                datePickerText.text = dateFormat.format(calendar.time)
                updateCalendar()
                dialog.dismiss()
            }
        }

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
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    }
                }
            }
            .show()
    }

    private fun showCategoryPickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_category_picker, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val gridView = dialogView.findViewById<GridView>(R.id.categoryGridView)

        val adapter = CategoryAdapter(this, transactionTypes)
        gridView.adapter = adapter

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = transactionTypes[position]
            categoryText.text = selectedCategory.name
            dialog.dismiss()
        }

        val addCategoryLayout = dialogView.findViewById<LinearLayout>(R.id.addCategoryLayout)
        addCategoryLayout.setOnClickListener {
            dialog.dismiss()
            // Gọi sang màn hình tạo danh mục mới:
            startActivity(Intent(this, AddFundActivity::class.java))
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.let { window ->
            val params = window.attributes
            params.gravity = Gravity.BOTTOM
            params.width = android.view.WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = params
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun showSuccessDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.success_dialog_transaction, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnBack: Button = dialogView.findViewById(R.id.btnBack)
        val btnCreateNew: Button = dialogView.findViewById(R.id.btnCreateNew)

        btnBack.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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