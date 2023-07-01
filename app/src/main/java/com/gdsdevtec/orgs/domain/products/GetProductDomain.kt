package com.gdsdevtec.orgs.domain.products

data class GetProductDomain(
    val success : Product? = null,
    val error : String? = null
)