package com.gdsdevtec.orgs.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id : Long = 0L,
    val name: String,
    val description: String,
    val value: BigDecimal,
    val image : String? = null,
    val date : String,
    val time : String
)