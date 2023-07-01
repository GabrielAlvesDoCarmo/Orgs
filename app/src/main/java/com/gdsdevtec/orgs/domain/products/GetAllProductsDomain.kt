package com.gdsdevtec.orgs.domain.products

data class GetAllProductsDomain(
    val success : List<Product>? = null,
    val error : String? = null
)