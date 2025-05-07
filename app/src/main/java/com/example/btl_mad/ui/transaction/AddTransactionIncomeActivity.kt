package com.example.btl_mad.ui.transaction

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
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.ExpenseRequest
import com.example.btl_mad.data.TransactionType
import com.example.btl_mad.data.User
import com.example.btl_mad.ui.fund.AddFundActivity
import com.example.btl_mad.ui.main.MainActivity
import com.example.btl_mad.utils.SharedPrefManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionIncomeActivity : AppCompatActivity() {

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
        setContentView(R.layout.add_transactions_income)

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
        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener{
            finish()
        }

        datePickerLayout.setOnClickListener {
            CustomDatePickerHelper(this) { selectedDate ->
                datePickerText.text = selectedDate
            }.show()
        }

        amountInput.setOnClickListener {
            amountInput.isCursorVisible = true
        }

        iconImage.setOnClickListener {
            TransactionDialogs.showImagePickerDialog(
                activity = this,
                externalCacheDir = externalCacheDir,
                packageName = packageName,
                onImageSelected = { uri -> cameraImageUri = uri },
                launchCamera = { intent -> takePictureLauncher.launch(intent) },
                launchGallery = { pickImageLauncher.launch("image/*") }
            )
        }

        // Xử lý phân loại
        categoryLayout.setOnClickListener {
            if (transactionTypes.isEmpty()) {
                fetchTransactionTypes()
            } else {
                TransactionDialogs.showCategoryPickerDialog(this, transactionTypes) { selected ->
                    categoryText.text = selected.name
                }
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
                saveIncomeToServer(date, amount, category, note)
            }
        }

        // Lấy danh sách transaction_type khi khởi động
//        fetchTransactionTypes()
    }

    override fun onResume() {
        super.onResume()
        fetchTransactionTypes()
    }


    private fun fetchTransactionTypes() {
        val userId = SharedPrefManager.getUserId(this)
        if (userId == -1) {
            Log.e("STATISTICS", "User ID not found in SharedPreferences")
            return
        }

        RetrofitClient.apiService.getTransactionTypesByQuery(userId).enqueue(object : Callback<List<TransactionType>> {
            override fun onResponse(call: Call<List<TransactionType>>, response: Response<List<TransactionType>>) {
                if (response.isSuccessful) {
                    transactionTypes = response.body() ?: emptyList()
                    if (transactionTypes.isEmpty()) {
                        Toast.makeText(this@AddTransactionIncomeActivity, "Không có phân loại nào!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AddTransactionIncomeActivity, "Lỗi khi lấy danh sách phân loại: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TransactionType>>, t: Throwable) {
                Toast.makeText(this@AddTransactionIncomeActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveIncomeToServer(date: String, amount: String, category: String, note: String) {
        val userId = SharedPrefManager.getUserId(this)
        if (userId == -1) {
            Log.e("STATISTICS", "User ID not found in SharedPreferences")
            return
        }

        val amountValue = amount.toDoubleOrNull() ?: 0.0
        val transactionTypeId = getTransactionTypeIdFromCategory(category)

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
            in_out_budget = "thu",
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
                        Toast.makeText(this@AddTransactionIncomeActivity, "Lưu giao dịch thành công!", Toast.LENGTH_SHORT).show()
                        showSuccessDialog()
                    } else {
                        Toast.makeText(this@AddTransactionIncomeActivity, "Lưu giao dịch thất bại: ${expenseResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AddTransactionIncomeActivity, "Lỗi server: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<com.example.btl_mad.data.ExpenseResponse>, t: Throwable) {
                Toast.makeText(this@AddTransactionIncomeActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTransactionTypeIdFromCategory(category: String): Int {
        return transactionTypes.find { it.name == category }?.id ?: 0
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