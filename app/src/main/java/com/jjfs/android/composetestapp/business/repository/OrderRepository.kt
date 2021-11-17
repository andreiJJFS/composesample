package com.jjfs.android.composetestapp.business.repository

import com.jjfs.android.composetestapp.Order_entity
import com.jjfs.android.composetestapp.business.cache.OrderCache
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.business.domain.models.operationResultFrom
import com.jjfs.android.composetestapp.ui.screens.main.ordersList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface OrderRepositoryInterface {
    fun getOrders(): Flow<OperationResult<List<Order>, Exception>>
    fun getOrderFromCache(id: String): Flow<OperationResult<Order_entity, Exception>>
    fun updateOrderInCache(
        id: String,
        description: String
    ): Flow<OperationResult<Unit, Exception>>
}

class OrderRepository(
    private val orderCache: OrderCache,
    private val authRepository: AuthRepository
): OrderRepositoryInterface {

    override fun getOrders(): Flow<OperationResult<List<Order>, Exception>> = flow {
        emit(OperationResult.InProgress())
        authRepository.getAccessToken()
        delay(1000)
        emit(OperationResult.Success(ordersList))
    }

    override fun getOrderFromCache(id: String): Flow<OperationResult<Order_entity, Exception>> =
        flow {
            emit(OperationResult.InProgress())
            val cachedOrder = operationResultFrom { orderCache.getOrder(id) }
            emit(cachedOrder)
        }

    override fun updateOrderInCache(
        id: String,
        description: String
    ): Flow<OperationResult<Unit, Exception>> = flow {
        emit(OperationResult.InProgress())
        val result = operationResultFrom { orderCache.addOrder(id, description) }
        emit(result)
    }
}
