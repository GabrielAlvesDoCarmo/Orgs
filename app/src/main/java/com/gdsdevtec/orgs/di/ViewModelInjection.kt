package com.gdsdevtec.orgs.di

import com.gdsdevtec.orgs.data.local.database.dao.ProductDao
import com.gdsdevtec.orgs.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelInjection {

    @Provides
    fun getRepositoryProducts(
        dao : ProductDao
    ) : ProductRepository{
        return ProductRepository(dao)
    }
}