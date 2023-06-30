package com.gdsdevtec.orgs.ui.form

import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gdsdevtec.orgs.databinding.ActivityFormBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.const.Constants
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.formatTimer
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
import com.gdsdevtec.orgs.utils.ext.message
import com.gdsdevtec.orgs.utils.ext.millisecondsToDate
import com.gdsdevtec.orgs.utils.ext.onClick
import com.gdsdevtec.orgs.utils.ext.setLayoutError
import com.gdsdevtec.orgs.utils.ext.stringForBigDecimal
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FormActivity : AppCompatActivity() {
    private val binding: ActivityFormBinding by lazy { ActivityFormBinding.inflate(layoutInflater) }
    private val dialog: DialogUtils by lazy { DialogUtils(this) }
    private val calendar by lazy { Calendar.getInstance() }
    @Inject lateinit var viewModel: FormViewModel
    private var product: Product? = null
    private var url: String? = null
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var dataPicker: MaterialDatePicker<Long>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getProductDb()
        setProductForEdit()
        setupActivity()
        observer()
    }
    private fun getProductDb() {
        viewModel.submitActions(
            FormActions.GetProductForId(
                intent.getLongExtra(Constants.PRODUCT_ID, 0L)
            )
        )
    }

    private fun setProductForEdit() = binding.run {
        product?.let { safeProduct ->
            url = safeProduct.image
            formImageProduct.loadImageDataWithUrl(dialog.imageLoader, safeProduct.image)
            inputProductEditName.setText(safeProduct.name)
            inputProductEditDescription.setText(safeProduct.description)
            inputProductEditValue.setText(safeProduct.value.toPlainString())
            inputProductEditDate.setText(safeProduct.date)
            inputProductEditHour.setText(safeProduct.time)
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is FormState.Loading -> binding.progressBarForm.show()
                    is FormState.SuccessGetProductForId -> {
                        binding.progressBarForm.hide()
                        product = state.product
                    }

                    is FormState.SaveProduct -> {
                        finish()
                    }

                    is FormState.Empty -> {
                        binding.progressBarForm.hide()
                    }

                    is FormState.ErrorFormProduct -> {
                        binding.progressBarForm.hide()
                        message(state.msg)
                    }
                }
            }
        }
    }

    private fun setupActivity() = with(binding) {
        timePicker = setupMaterialTimePicker()
        dataPicker = setupMaterialDatePicker()
        inputBtnSave.onClick {
            saveProduct()
        }
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
        viewModel.submitActions(
            FormActions.SaveProductForm(
                getProduct()
            )
        )
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

