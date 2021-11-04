package com.jjfs.android.composetestapp.business.network

import android.util.AndroidException
import android.util.Log
import com.jjfs.android.composetestapp.business.domain.models.User
import io.ktor.client.*
import io.ktor.client.request.*
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

interface AuthServiceInterface {
    suspend fun login(pin: String): User
}

class AuthService(private val client: HttpClient): AuthServiceInterface {
    val baseUrl = "http://testgoodsinapi.jjfoodservice.com/api/"

    override suspend fun login(pin: String): User {
        val tokenDTO: ApiResponse<TokenDTO> = client.post("${baseUrl}token/v1") { body = "v1" }
        Log.i("ApiService", tokenDTO.toString())
        val token: String = tokenDTO.data?.token ?: throw AndroidException("Missing token")
        return client.post<ApiResponse<UserDTO>>("${baseUrl}employee/v1/Login") {
            headers { append("JJ_TOKEN", token) }
            body = LoginRequest(Pin = pin, Version = "v1", Token = token)
        }.data?.let {
            Log.i("ApiService", it.toString())
            it.toUser(token)
        } ?: throw AndroidException()
    }
}

@Serializable
data class TokenDTO(
    @SerialName(value = "TOKENID") val tokenId: String = "",
    @SerialName(value = "TOKEN") val token: String = "",
    @SerialName(value = "CREATEDDATETIME") val createdDateTime: String = "",
    @SerialName(value = "EMPLOYEEID") val employeeId: String? = null,
    @SerialName(value = "LASTACTIVITYDATETIME") val lastActivityDateTime: String = "",
    @SerialName(value = "LASTAUTHENTICATEDDATETIME") val lastAuthenticatedDateTime: String = ""
)

@Serializable
data class LoginRequest(
    private val v: String = "v1",
    private val Pin: String,
    private val Version: String = "V1",
    private val Token: String
)

@Serializable
data class UserDTO(
    @SerialName(value = "PersonalNumber") val employeeId: String,
    @SerialName(value = "FirstName") val employeeName: String,
    @SerialName(value = "Warehouse") val locationId: String,
    @SerialName(value = "Department") val locationCode: String ? = null,
    val accessToken: String = "",
    @SerialName(value = "Roles") val roles: Set<String> = emptySet()
) {
    fun toUser(accessToken: String): User {
        return User(
            employeeId = this.employeeId,
            employeeName = this.employeeName,
            locationId = this.locationId,
            locationCode = this.locationCode ?: "",
            accessToken = accessToken,
            roles = this.roles
        )
    }
}

@Serializable
data class ApiResponse<T>(
    @SerialName(value = "Data") val data : T? = null,
    @SerialName(value = "Status") val status : Int? = null,
    @SerialName(value = "RequestUrl")val requestUrl : String? = null,
    @SerialName(value = "DateTime") var dateTime : String? = LocalDateTime.now().toString(),
    @SerialName(value = "Version") val version : String? = "V1",
    @SerialName(value = "Messages") var messages : MutableList<Message>? = null,
    @SerialName(value = "Error") val error : String? = null
)

@Serializable
data class Message(
    @SerialName(value = "Description")val description: String = "Initialized",
    @SerialName(value = "Type")val type: Int = 0,
    @SerialName(value = "Flag")val flag: Boolean = false,
    @SerialName(value = "Id")val id: String = "Init"
)
