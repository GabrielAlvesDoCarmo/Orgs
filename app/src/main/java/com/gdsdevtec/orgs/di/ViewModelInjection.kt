package com.gdsdevtec.orgs.di

import com.gdsdevtec.orgs.data.repository.ProductRepository
import com.gdsdevtec.orgs.data.usecase.ProductUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelInjection {

    @Provides
    fun productUseCase(
        repository: ProductRepository
    ): ProductUseCase {
        return ProductUseCase(repository)
    }
}