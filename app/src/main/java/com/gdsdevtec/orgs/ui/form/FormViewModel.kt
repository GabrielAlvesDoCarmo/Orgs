package com.gdsdevtec.orgs.ui.form

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

class FormViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    private var _state = MutableStateFlow<FormState>(FormState.Empty)
    val state: StateFlow<FormState> get() = _state

    fun submitActions(actions: FormActions) {
        when (actions) {
            is FormActions.SaveProductForm -> saveProduct(actions.product)
            is FormActions.GetProductForId -> getProductForId(actions.productID)
        }
    }

    private fun getProductForId(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                productRepository.getForId(id)
            }.onSuccess { stateSuccess ->
                stateSuccess.collect { productEntity ->
                    val product = productEntity?.let { it.toProductModel() }
                    _state.value = FormState.SuccessGetProductForId(product)
                }
            }.onFailure {
                _state.value = FormState.ErrorFormProduct("Falha ao carregar item ")
            }
        }
    }

    private fun saveProduct(product: Product) {
        _state.value = FormState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                productRepository.save(product.toProductEntity())
            }.onSuccess {
                _state.value = FormState.SaveProduct
            }.onFailure {
                _state.value = FormState.ErrorFormProduct("Falha ao salvar o producto")
            }
        }
    }
}