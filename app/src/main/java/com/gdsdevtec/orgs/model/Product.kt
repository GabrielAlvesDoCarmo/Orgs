package com.gdsdevtec.orgs.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
@Parcelize
data class Product(
    val name: String,
    val description: String,
    val value: BigDecimal,
    val image : String? = null,
    val date : String,
    val time : String
) : Parcelable