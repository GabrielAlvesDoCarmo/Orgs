package com.gdsdevtec.orgs.ui.main

import com.gdsdevtec.orgs.model.Product

sealed interface MainState{
    object Loading : MainState
    object Empty : MainState
    data class Success(val listProducts : List<Product>) : MainState
    data class Error(val msg : String) : MainState
}