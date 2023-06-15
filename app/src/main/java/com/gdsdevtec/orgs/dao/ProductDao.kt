package com.gdsdevtec.orgs.dao

import com.gdsdevtec.orgs.model.Product

object ProductDao {
    private val products = mutableListOf<Product>()

    fun add(product: Product){
        products.add(product)
    }

    fun getAllProducts() = products.toList()

    fun removeProduct(product: Product) = products.remove(product)

}