package com.gdsdevtec.orgs.ui

import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gdsdevtec.orgs.data.database.db.AppDatabase
import com.gdsdevtec.orgs.databinding.ActivityFormBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.const.Constants
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.formatTimer
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
import com.gdsdevtec.orgs.utils.ext.millisecondsToDate
import com.gdsdevtec.orgs.utils.ext.onClick
import com.gdsdevtec.orgs.utils.ext.setLayoutError
import com.gdsdevtec.orgs.utils.ext.stringForBigDecimal
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H


class FormActivity : AppCompatActivity() {
    private val binding: ActivityFormBinding by lazy {
        ActivityFormBinding.inflate(layoutInflater)
    }
    private val calendar by lazy { Calendar.getInstance() }
    private val dao by lazy { AppDatabase.getInstance(applicationContext).productDao() }
    private var productID: Long = 0L
    private var product: Product? = null
    private var url: String? = null
    private lateinit var dialog: DialogUtils
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var dataPicker: MaterialDatePicker<Long>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupActivity() = with(binding) {
        productID = getItemSelected()
        product = dao.getProductForId(productID)
        dialog = DialogUtils(this@FormActivity)
        timePicker = setupMaterialTimePicker()
        dataPicker = setupMaterialDatePicker()
        product?.let { safeProduct ->
            url = safeProduct.image
            formImageProduct.loadImageDataWithUrl(dialog.imageLoader, safeProduct.image)
            inputProductEditName.setText(safeProduct.name)
            inputProductEditDescription.setText(safeProduct.description)
            inputProductEditValue.setText(safeProduct.value.toPlainString())
            inputProductEditDate.setText(safeProduct.date)
            inputProductEditHour.setText(safeProduct.time)
        }
        inputBtnSave.onClick { saveProduct() }
        formImageProduct.onClick {
            showDialogImageProduct()
        }
        inputProductEditDate.onClick {
            selectedDateEvent()
        }
        inputProductEditHour.onClick {
            selectedTimeEvent()
        }
    }

    private fun showDialogImageProduct() = with(binding) {
        dialog.showDialog(
            urlDefault = url,
            resultUrl = { dialogUrl ->
                url = dialogUrl
                formImageProduct.loadImageDataWithUrl(dialog.imageLoader, url)
            }
        )
    }

    private fun selectedTimeEvent() {
        timePicker.show(supportFragmentManager, "TIME_PICKER")
        timePicker.addOnPositiveButtonClickListener {
            val timer = "${timePicker.hour.formatTimer()}:${timePicker.minute.formatTimer()}"
            binding.inputProductEditHour.setText(timer)
        }
    }

    private fun selectedDateEvent() {
//        TODO colocar o data range
        dataPicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        dataPicker.addOnPositiveButtonClickListener { milliseconds ->
            binding.inputProductEditDate.setText(milliseconds.millisecondsToDate())
        }
    }


    private fun saveProduct() {
        val isValidForm =
            validateName() && validateDescription() && validationDate() && validateTime()
        if (isValidForm) generateProduct()
    }

    private fun generateProduct() {
        dao.saveProduct(getProduct())
        finish()
    }

    private fun getProduct() = binding.run {
        return@run product?.let {
            Product(
                id = it.id,
                name = inputProductEditName.text.toString(),
                description = inputProductEditDescription.text.toString(),
                value = validateValue(),
                image = url,
                date = inputProductEditDate.text.toString(),
                time = inputProductEditHour.text.toString(),
            )
        } ?: run {
            Product(
                name = inputProductEditName.text.toString(),
                description = inputProductEditDescription.text.toString(),
                value = validateValue(),
                image = url,
                date = inputProductEditDate.text.toString(),
                time = inputProductEditHour.text.toString(),
            )
        }
    }

    private fun validateValue() = binding.run {
        inputProductEditValue.text.toString().stringForBigDecimal()
    }

    private fun validateTime(): Boolean = binding.run {
        val time = inputProductEditHour.text.toString()
        val actualTimer = calendar.get(Calendar.HOUR_OF_DAY)
        val actualMinutes = calendar.get(Calendar.MINUTE).formatTimer()
        return if (time.isEmpty()) {
            val timer = "$actualTimer:$actualMinutes"
            inputProductEditHour.setText(timer)
            true
        } else true
    }

    private fun validationDate(): Boolean = binding.run {
        val date = inputProductEditDate.text.toString()
        return if (date.isEmpty()) {
            inputProductEditDate.setText(System.currentTimeMillis().millisecondsToDate())
            true
        } else true
    }

    private fun validateDescription() = binding.run {
        val description = inputProductEditDescription.text.toString()
        return@run if (description.isEmpty()) inputProductLayoutDescription.setLayoutError(true)
        else inputProductLayoutDescription.setLayoutError(false)
    }

    private fun validateName() = binding.run {
        val name = inputProductEditName.text.toString()
        return@run if (name.isEmpty()) inputProductLayoutName.setLayoutError(true)
        else inputProductLayoutName.setLayoutError(false)
    }

    private fun getItemSelected(): Long {
        return intent.getLongExtra(Constants.PRODUCT_ID, 0L)
    }

    private fun setupMaterialDatePicker() = MaterialDatePicker
        .Builder
        .datePicker()
        .setTitleText("Seleciona a data do evento")
        .setSelection(System.currentTimeMillis())
        .build()

    private fun setupMaterialTimePicker() = MaterialTimePicker
        .Builder()
        .setTimeFormat(CLOCK_24H)
        .setInputMode(INPUT_MODE_CLOCK)
        .setTitleText("Selecione um horário")
        .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
        .build()
}

