package com.gdsdevtec.orgs.ui.detail

import com.gdsdevtec.orgs.model.Product

sealed interface DetailsState {
    data class Success(val product: Product? = null) : DetailsState
    data class Error(val msg: String) : DetailsState
    object Empty : DetailsState
    object ExcludedSuccess : DetailsState
}