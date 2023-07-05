package com.gdsdevtec.orgs.ui.main

import com.gdsdevtec.orgs.model.Product

sealed interface MainActions{
    object GetAllProducts : MainActions
    data class DeleteProduct(val product: Product) : MainActions
}