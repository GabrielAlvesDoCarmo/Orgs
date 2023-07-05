package com.gdsdevtec.orgs.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsdevtec.orgs.data.mapper.toProductModel
import com.gdsdevtec.orgs.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _state = MutableStateFlow<DetailsState>(DetailsState.Empty)
    val state: StateFlow<DetailsState> get() = _state
    fun submitActions(actions: DetailsActions) {
        when (actions) {
            is DetailsActions.GetProductForId -> getProductForId(actions.productId)
        }
    }

    private fun getProductForId(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                productRepository.getForId(productId)
            }.onSuccess {successResult->
                successResult.collect{entity->
                    val product = entity?.let { it.toProductModel() }
                    _state.value = DetailsState.Success(product)
                }
            }.onFailure {
                _state.value = DetailsState.Error("Falha ao abrir o producto")
            }
        }
    }
}