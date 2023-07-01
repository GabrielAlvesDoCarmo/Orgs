package com.gdsdevtec.orgs.ui.main

import com.gdsdevtec.orgs.domain.products.Product

sealed interface MainState{
    object Loading : MainState
    object Empty : MainState
    data class Success(val listProducts : List<Product>) : MainState
    data class Error(val msg : String) : MainState
    data class SuccessDelete(val msg : String) : MainState
}