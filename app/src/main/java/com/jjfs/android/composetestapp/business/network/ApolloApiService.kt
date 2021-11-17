package com.jjfs.android.composetestapp.business.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.request.RequestHeaders
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.jjfs.android.composetestapp.*
import com.jjfs.android.composetestapp.business.repository.AuthRepository
import com.jjfs.android.composetestapp.business.repository.LaunchDetails
import com.jjfs.android.composetestapp.business.repository.Site
import com.jjfs.android.composetestapp.business.repository.wsUrl
import okhttp3.OkHttpClient

const val WS_URL = "wss://apollo-fullstack-tutorial.herokuapp.com/graphql"
const val URL = "https://apollo-fullstack-tutorial.herokuapp.com"

class ApolloApiService(private val authRepository: AuthRepository) {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
    val client = ApolloClient.builder()
        .serverUrl(URL)
        .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory(wsUrl, okHttpClient))
        .okHttpClient(okHttpClient)
        .build()

    suspend fun fetchSites(cursor: String): List<Site> {
        val token = authRepository.getAccessToken()
        return client.query(LaunchListQuery(cursor = Input.fromNullable(cursor)))
            .buildAuthHeader(token)
            .await()
            .data?.launches?.launches
            ?.filterNotNull()
            ?.map { Site(it) } ?: throw Exception()
    }

    suspend fun fetchSiteDetails(id: String): LaunchDetails {
        val token = authRepository.getAccessToken()
        return client.query(LaunchDetailsQuery(id = id))
            .buildAuthHeader(token)
            .await()
            .data?.launch
            ?.let { LaunchDetails(it) } ?: throw Exception("Data doesn't exist")
    }

    suspend fun book(id: String): List<BookTripMutation.Launch?> {
        val token = authRepository.getAccessToken()
        return client.mutate(BookTripMutation(id = id))
            .buildAuthHeader(token)
            .await()
            .data?.bookTrips?.launches ?: throw Exception()
    }

    suspend fun cancel(id: String): List<CancelTripMutation.Launch?> {
        val token = authRepository.getAccessToken()
        return client.mutate(CancelTripMutation(id = id))
            .buildAuthHeader(token)
            .await()
            .data?.cancelTrip?.launches ?: throw Exception()
    }
}

private fun <T> ApolloMutationCall<T>.buildAuthHeader(token: String): ApolloMutationCall<T> {
    return toBuilder().requestHeaders(
        RequestHeaders.builder()
            .addHeader("Authorization", token)
            .build()
    ).build()
}


fun <T>ApolloQueryCall<T>.buildAuthHeader(token: String): ApolloQueryCall<T> {
    return toBuilder().requestHeaders(
        RequestHeaders.builder()
            .addHeader("Authorization", token)
            .build()
    ).build()
}