package com.gdsdevtec.orgs.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsdevtec.orgs.data.mapper.toProductEntity
import com.gdsdevtec.orgs.data.mapper.toProductModel
import com.gdsdevtec.orgs.data.repository.ProductRepository
import com.gdsdevtec.orgs.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<DetailsState>(DetailsState.Empty)
    val state: StateFlow<DetailsState> get() = _state
    fun submitActions(actions: DetailsActions) {
        when (actions) {
            is DetailsActions.GetProductForId -> getProductForId(actions.productId)
            is DetailsActions.ExcludedProduct -> excludedProduct(actions.product)
        }
    }

    private fun excludedProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
           runCatching {
               productRepository.delete(product.toProductEntity())
           }.onSuccess {
               _state.value = DetailsState.ExcludedSuccess
           }.onFailure {
               _state.value = DetailsState.Error("Falha ao excluir item")
           }
        }
    }

    private fun getProductForId(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                productRepository.getForId(productId)
            }.onSuccess { successResult ->
                successResult.collect { entity ->
                    val product = entity?.toProductModel()
                    _state.value = DetailsState.Success(product)
                }
            }.onFailure {
                _state.value = DetailsState.Error("Falha ao abrir o producto")
            }
        }
    }
}