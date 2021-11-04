package com.jjfs.android.composetestapp.business.domain.models

import com.jjfs.android.composetestapp.business.network.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Order(
    val orderId: String = "",
    val supplierName: String = "",
    val isApproved: Boolean = false,
    val orderDate: String = LocalDateTime.now().toString(),
    val orderSlot: String = "",
    val isBooked: Boolean = false,
    val itemCount: Int = 0,
    @Serializable(with = BigDecimalSerializer::class)
    val itemQuantity: BigDecimal = BigDecimal.ZERO
)