package com.gdsdevtec.orgs.data.usecase

import com.gdsdevtec.orgs.data.local.database.entity.ProductEntity
import com.gdsdevtec.orgs.domain.products.Product

fun ProductEntity.toProductModel() = Product(
    id = this.id,
    name = this.name,
    description = this.description,
    value = this.value,
    image = this.image,
    date = this.date,
    time = this.time
)
fun Product.toProductEntity() = ProductEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    value = this.value,
    image = this.image,
    date = this.date,
    time = this.time
)

fun List<ProductEntity>.toListProductModel(): List<Product> {
    return this.map {productModel->
        productModel.toProductModel()
    }
}