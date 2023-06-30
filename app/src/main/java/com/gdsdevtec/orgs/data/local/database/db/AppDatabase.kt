package com.gdsdevtec.orgs.data.local.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gdsdevtec.orgs.data.local.database.converter.Converters
import com.gdsdevtec.orgs.data.local.database.dao.ProductDao
import com.gdsdevtec.orgs.data.local.database.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}