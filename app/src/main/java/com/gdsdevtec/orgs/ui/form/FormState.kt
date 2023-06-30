package com.gdsdevtec.orgs.ui.form

import com.gdsdevtec.orgs.model.Product

sealed interface FormState{
    object Empty : FormState
    object SaveProduct : FormState
    object Loading : FormState
    data class SuccessGetProductForId(val product: Product? = null) : FormState
    data class ErrorFormProduct(val msg : String) : FormState
}