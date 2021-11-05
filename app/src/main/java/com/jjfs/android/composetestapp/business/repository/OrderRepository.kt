package com.jjfs.android.composetestapp.business.repository

import android.util.Log
import com.jjfs.android.composetestapp.Order_entity
import com.jjfs.android.composetestapp.business.cache.OrderCache
import com.jjfs.android.composetestapp.business.domain.models.OperationResult
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.business.domain.models.operationResultFrom
import com.jjfs.android.composetestapp.business.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface OrderRepositoryInterface {
    fun getOrders(): Flow<OperationResult<List<Order>, Exception>>
    fun getOrderFromCache(id: String): Flow<OperationResult<Order_entity, Exception>>
//    fun getOrderFromCache(id: String): Flow<OperationResult<Order, Exception>>
    fun updateOrderInCache(
        id: String,
        description: String
    ): Flow<OperationResult<Unit, Exception>>
}

class OrderRepository(
    private val apiService: ApiService,
    private val orderCache: OrderCache
): OrderRepositoryInterface {

    override fun getOrders(): Flow<OperationResult<List<Order>, Exception>> = flow {
        emit(OperationResult.InProgress())
        val result = operationResultFrom { apiService.getOrders() }
        Log.i("OrderRepository", result.toString())
        emit(operationResultFrom { apiService.getOrders() })
    }

//    override fun getOrderFromCache(id: String): Flow<OperationResult<Order, Exception>> =
//        flow {
//            emit(OperationResult.InProgress())
//            val cachedOrder = operationResultFrom { Order() }
//            emit(cachedOrder)
//        }

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
        val result = operationResultFrom {
//            orderCache.addOrder(id, description)
        }
        emit(result)
    }
}
