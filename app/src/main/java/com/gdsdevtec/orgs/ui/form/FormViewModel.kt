package com.gdsdevtec.orgs.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsdevtec.orgs.data.usecase.ProductUseCase
import com.gdsdevtec.orgs.domain.products.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

class FormViewModel @Inject constructor(
    private val productUseCase: ProductUseCase
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
            val result = productUseCase.getForId(id).single()
            when {
                result.success != null -> {
                    _state.value = FormState.SuccessGetProductForId(result.success)
                }

                result.error != null -> {
                    _state.value = FormState.ErrorFormProduct(result.error)
                }
            }
        }
    }

    private fun saveProduct(product: Product) {
        _state.value = FormState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val result = productUseCase.save(product).single()
            when {
                result.success != null -> {
                    _state.value = FormState.SaveProduct
                }

                result.error != null -> {
                    _state.value = FormState.ErrorFormProduct(result.error)
                }
            }
        }
    }
}