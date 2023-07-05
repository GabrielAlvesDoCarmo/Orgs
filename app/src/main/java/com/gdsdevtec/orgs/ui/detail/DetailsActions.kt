package com.gdsdevtec.orgs.ui.detail

import com.gdsdevtec.orgs.model.Product

sealed interface DetailsActions {
    data class GetProductForId(val productId: Long) : DetailsActions
    data class ExcludedProduct(val product: Product) :DetailsActions
}