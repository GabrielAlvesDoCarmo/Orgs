package com.gdsdevtec.orgs.ui

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
            dialog.showDialog(
                urlDefault = url,
                resultUrl = { dialogUrl ->
                    url = dialogUrl
                    formImageProduct.loadImageDataWithUrl(dialog.imageLoader, url)
                }
            )
        }
        inputProductEditDate.onClick {
            selectedDateEvent()
        }
        inputProductEditHour.onClick {
            selectedTimeEvent()
        }
    }

    private fun selectedTimeEvent() {
        val timePicker = MaterialTimePicker
            .Builder()
            .setTitleText("Selecione um horário")
            .setHour(4)
            .setMinute(20)
            .build()
        timePicker.show(supportFragmentManager, "TIME_PICKER")
        timePicker.addOnPositiveButtonClickListener {
            val timer = "${timePicker.hour}:${timePicker.minute}"
            binding.inputProductEditHour.setText(timer)
        }
    }

    private fun selectedDateEvent() {
//        TODO colocar o data range
        MaterialDatePicker.Builder.datePicker().setTitleText("Seleciona a data do evento").build().apply {
            addOnPositiveButtonClickListener {milliseconds->
                binding.inputProductEditDate.setText(milliseconds.millisecondsToDate())
            }
        }.show(supportFragmentManager, "MATERIAL_DATE_PICKER")


    }

    private fun saveProduct() {
        val isValidForm = validateName() && validateDescription() && validationDate()
        if (isValidForm) generateProduct()
    }

    private fun validationDate(): Boolean {
        return true
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
            image = url
        )
    }

    private fun validateValue() = binding.run {
        inputProductEditValue.text.toString().stringForBigDecimal()
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

