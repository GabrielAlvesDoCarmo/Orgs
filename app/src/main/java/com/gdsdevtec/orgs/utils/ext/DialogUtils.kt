package com.gdsdevtec.orgs.utils.ext

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.gdsdevtec.orgs.R
import com.gdsdevtec.orgs.databinding.DialogImageProductBinding

class DialogUtils(private val context: Context) {
    val imageLoader :ImageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()
    fun showDialog(
        resultUrl : (String?)->Unit,
        negativeButton : ()->Unit = {}
    )  {
        val binding = setupBindingDialog()
        AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                dialogConfirmClick(binding = binding, resultUrl = resultUrl)
            }
            .setNegativeButton(R.string.dialog_button_cancel) { _, _ ->
                negativeButton()
            }
            .show()
    }
    private fun setupBindingDialog(): DialogImageProductBinding {
        return DialogImageProductBinding.inflate(LayoutInflater.from(context)).apply {
            dialogBtnSearchImg.onClick {
                if (validateDialogUrl(this)) setImageForm(this)
            }
            inputProductLayoutImageUrl.setEndIconOnClickListener {
                clickEndIconUrlDialog(this)
            }
        }
    }

    private fun validateDialogUrl(binding: DialogImageProductBinding): Boolean {
        return with(binding) {
            val url = inputProductImageUrl.text.toString()
            return@with if (url.isEmpty()) inputProductLayoutImageUrl.setLayoutError(true)
            else inputProductLayoutImageUrl.setLayoutError(false)
        }
    }

    private fun setImageForm(binding: DialogImageProductBinding)  = binding.apply{
        dialogImg.loadImageDataWithUrl(
            imageLoader = imageLoader,
            url = inputProductImageUrl.text.toString()
        )
    }
    private fun clickEndIconUrlDialog(binding: DialogImageProductBinding) = binding.apply{
        inputProductImageUrl.text?.clear()
        dialogImg.setImageResource(R.drawable.image_default)
    }



    private fun dialogConfirmClick(binding: DialogImageProductBinding,resultUrl: (String?) -> Unit) = binding.apply {
        if (inputProductImageUrl.text.isNullOrEmpty()){
            return@apply
        }else{
            inputProductLayoutImageUrl.setLayoutError(false)
            resultUrl(inputProductImageUrl.text.toString())
        }
    }
}