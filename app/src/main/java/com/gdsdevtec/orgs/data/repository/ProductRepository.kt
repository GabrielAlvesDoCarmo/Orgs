package com.gdsdevtec.orgs.data.repository

import com.gdsdevtec.orgs.data.local.database.dao.ProductDao
import com.gdsdevtec.orgs.data.local.database.entity.ProductEntity
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val dao: ProductDao
) {
     fun getAllProducts() = dao.getAllProduct()
     suspend fun save(product : ProductEntity) = dao.saveProduct(product)
     suspend fun delete(product : ProductEntity) = dao.deleteProduct(product)
     fun getForId(id : Long) = dao.getProductForId(id)
}