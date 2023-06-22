package com.gdsdevtec.orgs.ui

import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityFormBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
import com.gdsdevtec.orgs.utils.ext.millisecondsToDate
import com.gdsdevtec.orgs.utils.ext.onClick
import com.gdsdevtec.orgs.utils.ext.setLayoutError
import com.gdsdevtec.orgs.utils.ext.stringForBigDecimal
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H


class FormActivity : AppCompatActivity() {
    private val binding: ActivityFormBinding by lazy {
        ActivityFormBinding.inflate(layoutInflater)
    }
    private var url: String? = null
    private lateinit var dialog: DialogUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
    }

    private fun setupActivity() = with(binding) {
        dialog = DialogUtils(this@FormActivity)
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
        val timePicker = MaterialTimePicker
            .Builder()
            .setTimeFormat(CLOCK_24H)
            .setInputMode(INPUT_MODE_CLOCK)
            .setTitleText("Selecione um horário")
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            .build()
        timePicker.show(supportFragmentManager, "TIME_PICKER")
        timePicker.addOnPositiveButtonClickListener {
            val timer = "${timePicker.hour}:${timePicker.minute}"
            binding.inputProductEditHour.setText(timer)
        }
    }

    private fun selectedDateEvent() {
//        TODO colocar o data range
        val materialDatePicker = MaterialDatePicker
            .Builder
            .datePicker()
            .setTitleText("Seleciona a data do evento")
            .setSelection(System.currentTimeMillis())
            .build()
        materialDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        materialDatePicker.addOnPositiveButtonClickListener {milliseconds->
            binding.inputProductEditDate.setText(milliseconds.millisecondsToDate())
        }
    }

    private fun saveProduct() {
        val isValidForm =
            validateName() && validateDescription() && validationDate() && validateTime()
        if (isValidForm) generateProduct()
    }

    private fun generateProduct() {
        ProductDao.add(getProduct())
        finish()
    }

    private fun getProduct() = binding.run {
        return@run Product(
            name = inputProductEditName.text.toString(),
            description = inputProductEditDescription.text.toString(),
            value = validateValue(),
            image = url,
            date = inputProductEditDate.text.toString(),
            time = inputProductEditHour.text.toString(),
        )
    }

    private fun validateValue() = binding.run {
        inputProductEditValue.text.toString().stringForBigDecimal()
    }

    private fun validateTime(): Boolean = binding.run {
        val time = inputProductEditHour.text.toString()
        return if (time.isEmpty()) inputProductLayoutHours.setLayoutError(true)
        else inputProductLayoutHours.setLayoutError(false)
    }

    private fun validationDate(): Boolean = binding.run {
        val date = inputProductEditDate.text.toString()
        return if (date.isEmpty()) inputProductLayoutDate.setLayoutError(true)
        else inputProductLayoutDate.setLayoutError(false)
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
}

