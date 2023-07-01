package com.gdsdevtec.orgs.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsdevtec.orgs.data.usecase.ProductUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val productUseCase: ProductUseCase
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
            val result = productUseCase.getForId(productId).single()
            when{
                result.success!= null->{
                    _state.value = DetailsState.Success(result.success)
                }
                result.error!= null->{
                    _state.value = DetailsState.Error(result.error)
                }
            }
        }
    }
}