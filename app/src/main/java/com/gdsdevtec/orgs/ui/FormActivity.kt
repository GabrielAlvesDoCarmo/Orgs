package com.gdsdevtec.orgs.ui

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityFormBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.onClick
import com.google.android.material.textfield.TextInputLayout
import java.math.BigDecimal

class FormActivity : AppCompatActivity() {
    private val binding: ActivityFormBinding by lazy {
        ActivityFormBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
    }

    private fun setupActivity() = with(binding){
        inputBtnSave.onClick { saveProduct() }
        formImageProduct.onClick { showDialogProduct() }

    }

    private fun showDialogProduct() {
        AlertDialog.Builder(this)
            .setView(R.layout.dialog_image_product)
            .setPositiveButton(R.string.dialog_button_confirm){_,_->

            }
            .setNegativeButton(R.string.dialog_button_cancel){_,_->

            }
            .show()
    }

    private fun saveProduct() {
        val isValidForm = validateName() && validateDescription()
        if (isValidForm) generateProduct()
    }

    private fun generateProduct() = binding.run {
        val product = Product(
            name = inputProductEditName.text.toString(),
            description = inputProductEditDescription.text.toString(),
            value = validateValue()
        )
        ProductDao.add(product)
        finish()
    }

    private fun validateValue() = binding.run {
        val value = inputProductEditValue.text.toString()
        return@run if (value.isNotEmpty()) BigDecimal(value) else BigDecimal.ZERO
    }

    private fun validateDescription() = binding.run {
        val description = inputProductEditDescription.text.toString()
        return@run if (description.isEmpty()) setLayoutError(true,inputProductLayoutDescription) else setLayoutError(false,inputProductLayoutDescription)
    }

    private fun validateName() = binding.run {
        val name = inputProductEditName.text.toString()
        return@run if (name.isEmpty()) setLayoutError(true,inputProductLayoutName) else  setLayoutError(false,inputProductLayoutName)
    }

    private fun setLayoutError(isError: Boolean, layout: TextInputLayout) = if (isError) {
        layout.isErrorEnabled = true
        layout.error = "Campo em branco"
        false
    } else {
        layout.isErrorEnabled = false
        true
    }
}