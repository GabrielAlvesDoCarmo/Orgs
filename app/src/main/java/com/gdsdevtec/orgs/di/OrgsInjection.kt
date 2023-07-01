package com.gdsdevtec.orgs.di

import android.content.Context
import androidx.room.Room
import com.gdsdevtec.orgs.data.local.database.dao.ProductDao
import com.gdsdevtec.orgs.data.local.database.db.AppDatabase
import com.gdsdevtec.orgs.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OrgsInjection {

    @Singleton
    @Provides
    fun getInstance(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "orgs.db"
        ).build()
    }

    @Singleton
    @Provides
    fun getProductDao(db: AppDatabase) = db.productDao()

    @Provides
    fun getRepositoryProducts(
        dao : ProductDao
    ) : ProductRepository {
        return ProductRepository(dao)
    }
}