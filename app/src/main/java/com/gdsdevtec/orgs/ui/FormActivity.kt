package com.gdsdevtec.orgs.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.dao.ProductDao
import com.gdsdevtec.orgs.databinding.ActivityFormBinding
import com.gdsdevtec.orgs.databinding.DialogImageProductBinding
import com.gdsdevtec.orgs.model.Product
import com.gdsdevtec.orgs.utils.ext.DialogUtils
import com.gdsdevtec.orgs.utils.ext.onClick
import com.google.android.material.textfield.TextInputLayout
import java.math.BigDecimal

class FormActivity : AppCompatActivity() {
    private val binding: ActivityFormBinding by lazy {
        ActivityFormBinding.inflate(layoutInflater)
    }
    private var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActivity()
    }

    private fun setupActivity() = with(binding) {
        inputBtnSave.onClick { saveProduct() }
        formImageProduct.onClick { showDialogProduct() }
    }

    private fun showDialogProduct() {
        val dialogBinding = setupBindingDialog()
        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                dialogConfirmClick(dialogBinding)
            }
            .setNegativeButton(R.string.dialog_button_cancel) { _, _ ->
                binding.formImageProduct.setImageResource(R.drawable.ic_not_image_default)
            }
            .show()
    }
    private fun setupBindingDialog(): DialogImageProductBinding {
        return DialogImageProductBinding.inflate(layoutInflater).apply {
            dialogBtnSearchImg.onClick {
                if (validateDialogUrl(this)) setImageForm(this)
            }
            inputProductLayoutImageUrl.setEndIconOnClickListener {
                clickEndIconUrlDialog(this)
            }
        }
    }
    private fun setImageForm(dialog: DialogImageProductBinding) = with(dialog){
        url = inputProductImageUrl.text.toString()
        dialogImg.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            load(url, imageLoader = DialogUtils.getImageLoader(this@FormActivity)){
                fallback(R.drawable.ic_error_image_null)
                error(R.drawable.ic_error_image_value).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            }
        }
    }

    private fun dialogConfirmClick(dialogBinding: DialogImageProductBinding) {
        if (dialogBinding.inputProductImageUrl.text.isNullOrEmpty()){
            return
        }else{
            setLayoutError(false,dialogBinding.inputProductLayoutImageUrl)
            binding.formImageProduct.apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                load(dialogBinding.inputProductImageUrl.text.toString(), imageLoader = DialogUtils.getImageLoader(this@FormActivity)){
                    fallback(R.drawable.ic_error_image_null)
                    error(R.drawable.ic_error_image_value).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                }
            }
        }
    }


    private fun clickEndIconUrlDialog(dialog: DialogImageProductBinding) = with(dialog){
        inputProductImageUrl.text?.clear()
        dialogImg.scaleType = ImageView.ScaleType.FIT_CENTER
        dialogImg.setImageResource(R.drawable.ic_not_image_default)
    }


    private fun validateDialogUrl(dialog: DialogImageProductBinding) = with(dialog) {
        val url = inputProductImageUrl.text.toString()
        return@with if (url.isEmpty()) setLayoutError(true, inputProductLayoutImageUrl)
        else setLayoutError(false, inputProductLayoutImageUrl)
    }

    private fun saveProduct() {
        val isValidForm = validateName() && validateDescription()
        if (isValidForm) generateProduct()
    }

    private fun generateProduct() {
        ProductDao.add(getProduct())
        finish()
    }

    private fun getProduct()  = binding.run{
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
        return@run if (description.isEmpty()) setLayoutError(
            true,
            inputProductLayoutDescription
        ) else setLayoutError(false, inputProductLayoutDescription)
    }

    private fun validateName() = binding.run {
        val name = inputProductEditName.text.toString()
        return@run if (name.isEmpty()) setLayoutError(
            true,
            inputProductLayoutName
        ) else setLayoutError(false, inputProductLayoutName)
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

//"https://minhasaude.proteste.org.br/wp-content/uploads/2022/10/muitas-laranjas.png.webp"