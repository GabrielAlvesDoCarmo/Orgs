package com.gdsdevtec.orgs.di

import com.gdsdevtec.orgs.data.usecase.ProductUseCase
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
        productUseCase: ProductUseCase
    ) : MainViewModel{
        return MainViewModel(productUseCase)
    }

    @Provides
    fun getFormViewModel(
        productUseCase: ProductUseCase
    ) : FormViewModel{
        return FormViewModel(productUseCase)
    }
    @Provides
    fun getDetailsViewModel(
        productUseCase: ProductUseCase
    ) : DetailsViewModel{
        return DetailsViewModel(productUseCase)
    }
}