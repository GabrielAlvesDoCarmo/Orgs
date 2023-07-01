package com.gdsdevtec.orgs.data.usecase

import com.gdsdevtec.orgs.data.local.database.entity.ProductEntity
import com.gdsdevtec.orgs.data.repository.ProductRepository
import com.gdsdevtec.orgs.domain.products.GetAllProductsDomain
import com.gdsdevtec.orgs.domain.products.Product
import com.gdsdevtec.orgs.domain.products.ChangeProductDomain
import com.gdsdevtec.orgs.domain.products.GetProductDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import javax.inject.Inject

class ProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    fun getAllProducts() = flow<GetAllProductsDomain> {
        runCatching {
            repository.getAllProducts()
        }.onSuccess { success ->
            successAllProducts(success)
        }.onFailure {
            emit(GetAllProductsDomain(error = "Falha ao buscar produtos"))
        }
    }

    private suspend fun successAllProducts(
        success: Flow<List<ProductEntity>?>
    ) = flow {
        success.collect { listProducts ->
            val products = listProducts?.let { return@let it.toListProductModel() } ?: listOf()
            emit(GetAllProductsDomain(success = products))
        }
    }

    suspend fun save(product: Product) = flow {
        runCatching {
            repository.save(product.toProductEntity())
        }.onSuccess {
            emit(ChangeProductDomain(success = "Salvo com sucesso"))
        }.onFailure {
            emit(ChangeProductDomain(error = "Falha ao salvar"))
        }
    }

    suspend fun delete(product: Product) = flow {
        runCatching {
            repository.delete(product.toProductEntity())
        }.onSuccess {
            emit(ChangeProductDomain(success = "Excluido com sucesso"))
        }.onFailure {
            emit(ChangeProductDomain(error = "Erro ao excluir"))
        }.getOrThrow()
    }

    fun getForId(id: Long) = flow<GetProductDomain> {
        runCatching {
            repository.getForId(id)
        }.onSuccess { success ->
            val entity = success.single()
            emit(GetProductDomain(success = entity?.toProductModel()))
        }.onFailure {
            emit(GetProductDomain(error = "Erro ao buscar produto"))
        }
    }
}