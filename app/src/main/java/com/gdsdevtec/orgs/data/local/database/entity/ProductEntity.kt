package com.gdsdevtec.orgs.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "PRODUCT")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id : Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "value") val value: BigDecimal,
    @ColumnInfo(name = "image") val image : String? = null,
    @ColumnInfo(name = "date") val date : String,
    @ColumnInfo(name = "time") val time : String
)
