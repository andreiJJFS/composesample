package com.jjfs.android.composetestapp.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.business.repository.NetworkService
import com.jjfs.android.composetestapp.business.repository.OrderRepository
import com.jjfs.android.composetestapp.business.repository.UserNotAuthenticatedException
import com.jjfs.android.composetestapp.ui.Screen
import com.jjfs.android.composetestapp.ui.utils.Event
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class MainViewModel(
    private val orderRepository: OrderRepository,
    private val networkService: NetworkService
): ViewModel() {

    private val _stateFlow = MutableStateFlow(MainViewModelState())
    val stateFlow = _stateFlow.asStateFlow()

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
       getOrders()
    }

    fun getOrders() {
        orderRepository.getOrders()
            .onEach { result ->
                when(result) {
                    is OperationResult.Success -> {
                        _stateFlow.value = MainViewModelState(orders = ordersList)
                    }
                    is OperationResult.Failure -> {
                        if(result.reason is UserNotAuthenticatedException)
                            _eventChannel.send(Event.OpenLogin { getOrders() })
                        else
                            _stateFlow.value = MainViewModelState(error = result.reason.message ?: "Error")
                    }
                    is OperationResult.InProgress -> {
                        _stateFlow.value = MainViewModelState(isLoading = true)
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    fun swipeRight(orderId: String) {
        Log.i("Mainviewmodel", "swipe right!")
        val arg = stateFlow.value.orders.first { it.orderId == orderId }
         viewModelScope.launch {
             _eventChannel.send(Event.Navigate(
                 Screen.Detail,
                 Json.encodeToString(arg)
             ))
         }
    }

    fun swipeLeft(orderId: String) {
        Log.i("Mainviewmodel", "swipe left!")
        removeOrder(orderId)
    }

    fun removeOrder(orderId: String) {
        val index = stateFlow.value.orders.indexOfFirst { it.orderId == orderId }
        val newList = _stateFlow.value.orders.toMutableList().also { it.removeAt(index)  }
        _stateFlow.value =  _stateFlow.value.copy(orders = newList)
    }
}

data class MainViewModelState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String = ""
)

data class OrderItem(
    val orderId: String = "",
    val supplierName: String = "",
    val orderSlot: String = "",
    val itemCount: Int = 0,
    val orderDate: String = LocalDateTime.now().toString(),
) {

    constructor(order: Order): this(
        orderId = order.orderId,
        supplierName = order.supplierName,
        orderSlot = order.orderSlot,
        itemCount = order.itemCount,
        orderDate = order.orderDate
    )
}

val ordersList = listOf(
    Order(
        orderId = "ID1234",
        supplierName = "Supplier One"
    ),
    Order(
        orderId = "ID4356",
        supplierName = "Supplier Two"
    ),
    Order(
        orderId = "ID6523",
        supplierName = "Supplier Three"
    ),
    Order(
        orderId = "ID7456",
        supplierName = "Supplier Four"
    ),
)