package com.gdsdevtec.orgs.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.imageLoader
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityFormBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.loadImageDataWithUrl
import com.gdsdevtec.orgs.utils.ext.onClick
import com.gdsdevtec.orgs.utils.ext.setLayoutError
import java.math.BigDecimal

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
                resultUrl = { dialogUrl ->
                    url = dialogUrl
                    formImageProduct.loadImageDataWithUrl(imageLoader, url)
                },
                negativeButton = {
                    formImageProduct.run {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        setImageResource(R.drawable.ic_not_image_default)
                    }
                }
            )
        }
    }

    private fun saveProduct() {
        val isValidForm = validateName() && validateDescription()
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
            image = url
        )
    }

    private fun validateValue() = binding.run {
        val value = inputProductEditValue.text.toString()
        return@run if (value.isNotEmpty()) BigDecimal(value) else BigDecimal.ZERO
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

