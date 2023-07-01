package com.gdsdevtec.orgs.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsdevtec.orgs.data.usecase.ProductUseCase
import com.gdsdevtec.orgs.domain.products.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val productUseCase: ProductUseCase
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
            productUseCase.delete(product).collect {
                if (it.success != null) {
                    _state.value = MainState.SuccessDelete(it.success)
                }
                if (it.error != null) {
                    _state.value = MainState.Error(it.error)
                }
            }
        }
    }

    private fun getAllProducts() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            productUseCase.getAllProducts().collect { result ->
                when {
                    result.success != null -> {
                        _state.value = MainState.Success(result.success)
                    }

                    result.error != null -> {
                        _state.value = MainState.Error(result.error)
                    }
                }
            }
        }
    }
}