package com.gdsdevtec.orgs.utils.ext

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun AppCompatActivity.nextScreen(activity: AppCompatActivity) = startActivity(Intent(this,activity::class.java))

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