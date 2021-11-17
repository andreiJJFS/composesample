package com.jjfs.android.composetestapp.ui.screens.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.business.domain.models.onFailure
import com.jjfs.android.composetestapp.business.domain.models.onSuccess
import com.jjfs.android.composetestapp.business.repository.OrderRepository
import com.jjfs.android.composetestapp.ui.components.CommonViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DetailViewModel(private val orderRepository: OrderRepository) : CommonViewModel() {

    private val _stateFlow = MutableStateFlow(DetailViewModelState())
    val stateFlow = _stateFlow.asStateFlow()

    fun setOrder(order: Order) {
        _stateFlow.value = _stateFlow.value.copy(order = order)
    }

    fun getCachedOrder() {
        orderRepository.getOrderFromCache(stateFlow.value.order.orderId)
            .onEach { result ->
                result.onSuccess {
                    _stateFlow.value = _stateFlow.value.copy(description = it.description)
                }
                result.onFailure { Log.i("detailViewModel", it.message ?: "") }
            }
            .launchIn(viewModelScope)
    }

    fun updateDescription(description: String) {
        orderRepository.updateOrderInCache(_stateFlow.value.order.orderId, description)
            .onEach { result ->
                result.onSuccess { getCachedOrder() }
                result.onFailure { Log.i("detailViewModel", it.message ?: "") }
            }
            .launchIn(viewModelScope)
    }
}

data class DetailViewModelState(
    val description: String = "Missing description",
    val order: Order = Order(),
    val isLoading: Boolean = false,
    val error: String = ""
)
