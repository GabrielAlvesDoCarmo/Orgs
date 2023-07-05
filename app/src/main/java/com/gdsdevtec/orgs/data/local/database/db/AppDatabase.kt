package com.gdsdevtec.orgs.data.local.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gdsdevtec.orgs.data.local.database.converter.Converters
import com.gdsdevtec.orgs.data.local.database.dao.ProductDao
import com.gdsdevtec.orgs.data.local.database.dao.UserDao
import com.gdsdevtec.orgs.data.local.database.entity.ProductEntity
import com.gdsdevtec.orgs.data.local.database.entity.UserEntity

@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class
    ], version = 2, exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
}