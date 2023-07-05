package com.gdsdevtec.orgs.data.local.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATIONS_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `USUARIO` 
            (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
             `user_name` TEXT NOT NULL,
             `name` TEXT NOT NULL,
             `password` TEXT NOT NULL)""")
    }
}