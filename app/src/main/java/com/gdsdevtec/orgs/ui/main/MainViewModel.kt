package com.gdsdevtec.orgs.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsdevtec.orgs.data.local.database.entity.ProductEntity
import com.gdsdevtec.orgs.data.mapper.toListProductModel
import com.gdsdevtec.orgs.data.mapper.toProductEntity
import com.gdsdevtec.orgs.data.repository.ProductRepository
import com.gdsdevtec.orgs.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _state = MutableStateFlow<MainState>(MainState.Empty)
    val state: StateFlow<MainState> get() = _state
    fun submitActions(actions: MainActions) {
        when (actions) {
            is MainActions.GetAllProducts -> getAllProducts()
            is MainActions.DeleteProduct -> deleteProducts(actions.product)
        }
    }

    private fun deleteProducts(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                productRepository.delete(product.toProductEntity())
            }.onSuccess {
                getProductsDB().collect{
                    _state.value = MainState.Success(it.toListProductModel())
                }
            }.onFailure {
                _state.value = MainState.Error("Falha ao excluir produtos")
            }
        }
    }

    private fun getAllProducts() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            getProductsDB()
        }
    }

    private suspend fun getProductsDB(): Flow<List<ProductEntity>?> {
        return runCatching {
            productRepository.getAllProducts()
        }.onSuccess { resultSuccess ->
            getSuccessResult(resultSuccess)
        }.onFailure {
            _state.value = MainState.Error("Falha ao buscar os produtos")
        }.getOrThrow()
    }

    private suspend fun getSuccessResult(resultSuccess: Flow<List<ProductEntity>?>) {
        resultSuccess.collect { listEntity ->
            _state.value = MainState.Success(listEntity.toListProductModel())
        }
    }
}