package com.jjfs.android.composetestapp.business.network

import android.os.Parcelable
import android.util.AndroidException
import android.util.Log
import com.jjfs.android.composetestapp.business.domain.models.Order
import com.jjfs.android.composetestapp.business.domain.models.User
import com.jjfs.android.composetestapp.business.repository.AuthRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.LocalDateTime

interface ApiServiceInterface {
    suspend fun getOrders(pin: String? = null): List<Order>
}
const val APIKEY = "Adsfdfasdfasdfasdfs"

class ApiService(
    private val client: HttpClient,
    private val authRepository: AuthRepository
): ApiServiceInterface {
    val baseUrl = "http://testgoodsinapi.jjfoodservice.com/api/"

    override suspend fun getOrders(search: String?): List<Order> {
        val url = baseUrl + "order/v1/zoneNonBook"
        val accessToken = authRepository.getAccessToken()
        val user = authRepository.getUser()
        return client.get<ApiResponse<List<OrderDTO>>>(url) {
            headers { append(HttpHeaders.Authorization, "Bearer $accessToken") }
            parameter("Employee", user.employeeId)
            parameter("Warehouse", user.locationId)
            parameter("Search", search)
            parameter("Version", "V1")
            parameter("Token", accessToken)
            parameter("ApiKey", APIKEY)
        }.data?.toOrderList() ?: emptyList()
    }
}

@Serializable
data class OrderDTO(
    @SerialName(value = "OrderId") val orderId: String = "",
    @SerialName(value = "SupplierName") val supplierName: String = "",
    @SerialName(value = "IsApproved") val isApproved: Boolean = false,
    @SerialName(value = "OrderDate") val orderDate: String = LocalDateTime.now().toString(),
    @SerialName(value = "OrderSlot") val orderSlot: String = "",
    @SerialName(value = "IsBooked") val isBooked: Boolean = false,
    @SerialName(value = "ItemCount") val itemCount: Int = 0,
    @Serializable(with = BigDecimalSerializer::class)
    @SerialName(value = "ItemQuantity") val itemQuantity: BigDecimal = BigDecimal.ZERO
)

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: BigDecimal) { encoder.encodeString(value.toString()) }
    override fun deserialize(decoder: Decoder): BigDecimal { return BigDecimal(decoder.decodeString()) }
}

fun OrderDTO.toOrder(): Order =
    Order(
        orderId = orderId,
        supplierName = supplierName,
        isApproved = isApproved,
        orderDate =  orderDate,
        orderSlot = orderSlot,
        isBooked = isBooked,
        itemCount =  itemCount,
        itemQuantity =  itemQuantity
    )

fun List<OrderDTO>.toOrderList(): List<Order> = map { it.toOrder() }