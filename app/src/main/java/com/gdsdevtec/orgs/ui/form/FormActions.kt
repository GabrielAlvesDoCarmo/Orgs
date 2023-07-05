package com.gdsdevtec.orgs.ui.form

import com.gdsdevtec.orgs.model.Product

sealed interface FormActions{
    data class SaveProductForm(val product: Product) : FormActions
    data class GetProductForId(val productID: Long) : FormActions
}