package com.gdsdevtec.orgs.utils.ext

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

fun AppCompatActivity.nextScreen(activity: AppCompatActivity) = startActivity(Intent(this,activity::class.java))

inline fun<reified T> AppCompatButton.onClick(crossinline action : ()-> T) {
    this.setOnClickListener { action.invoke() }
}