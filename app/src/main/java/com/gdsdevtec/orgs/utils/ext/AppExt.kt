package com.gdsdevtec.orgs.utils.ext

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import coil.ImageLoader
import coil.load
import com.gdsdevtec.orgs.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun AppCompatActivity.nextScreen(activity: AppCompatActivity) = startActivity(Intent(this,activity::class.java))

fun AppCompatActivity.message(msg :String) = Toast.makeText(this,msg,Toast.LENGTH_LONG).show()

inline fun<reified T> View.onClick(crossinline action : ()-> T) {
    this.setOnClickListener { action.invoke() }
}
fun BigDecimal.convertBigDecimalForCurrencyLocale(): String {
    return try {
        val currencyLocale= NumberFormat.getCurrencyInstance(Locale("pt", "br"))
        currencyLocale.format(this).toString()
    }catch (e : Exception){
        "Erro"
    }
}

fun AppCompatImageView.loadImageDataWithUrl(imageLoader: ImageLoader,url : String?) {
    scaleType = ImageView.ScaleType.CENTER_CROP
    load(url, imageLoader = imageLoader) {
        placeholder(R.drawable.playceholder)
        fallback(R.drawable.ic_error_image_null)
        error(R.drawable.ic_error_image_value).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }
}