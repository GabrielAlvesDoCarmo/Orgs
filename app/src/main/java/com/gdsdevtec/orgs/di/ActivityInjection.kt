package com.gdsdevtec.orgs.di

import com.gdsdevtec.orgs.data.repository.ProductRepository
import com.gdsdevtec.orgs.ui.detail.DetailsViewModel
import com.gdsdevtec.orgs.ui.form.FormViewModel
import com.gdsdevtec.orgs.ui.main.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class ActivityInjection {

    @Provides
    fun getMainViewModel(
        productRepository: ProductRepository
    ) : MainViewModel{
        return MainViewModel(productRepository)
    }

    @Provides
    fun getFormViewModel(
        productRepository: ProductRepository
    ) : FormViewModel{
        return FormViewModel(productRepository)
    }
    @Provides
    fun getDetailsViewModel(
        productRepository: ProductRepository
    ) : DetailsViewModel{
        return DetailsViewModel(productRepository)
    }
}