package com.gdsdevtec.orgs.ui.detail

sealed interface DetailsActions {
    data class GetProductForId(val productId: Long) : DetailsActions
}