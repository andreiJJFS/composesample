package com.jjfs.android.composetestapp.business.network

import android.util.AndroidException
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.jjfs.android.composetestapp.LoginMutation
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
import okhttp3.OkHttpClient
import java.math.BigDecimal
import java.time.LocalDateTime

interface AuthServiceInterface {
    suspend fun login(email: String = "me@example.com"): String
}

class AuthService: AuthServiceInterface {
    private val baseUrl = "https://apollo-fullstack-tutorial.herokuapp.com"

    private val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(baseUrl)
        .build()

    override suspend fun login(email: String): String {
        val response =  apolloClient.mutate(LoginMutation(email = Input.fromNullable(email))).await()
        return response.data?.login ?: throw Exception("Invalid login")
    }
}
