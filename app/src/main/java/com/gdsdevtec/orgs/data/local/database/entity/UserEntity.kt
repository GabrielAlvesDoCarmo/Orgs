package com.gdsdevtec.orgs.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

import androidx.room.PrimaryKey

@Entity(tableName = "USUARIO")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id : Long = 0L,
    @ColumnInfo(name = "user_name") val userName : String,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo("password") val password : String,
)