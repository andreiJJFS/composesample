package com.jjfs.android.composetestapp.business.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Order(
    val orderId: String = "",
    val supplierName: String = "",
    val isApproved: Boolean = false,
    val orderDate: String = LocalDateTime.now().toString(),
    val orderSlot: String = "",
    val isBooked: Boolean = false,
    val itemCount: Int = 0,
    val itemQuantity: BigDecimal = BigDecimal.ZERO
)