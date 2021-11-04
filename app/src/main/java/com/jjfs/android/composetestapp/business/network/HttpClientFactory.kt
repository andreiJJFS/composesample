package com.jjfs.android.composetestapp.business.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*

object HttpClientFactory {
    private const val key = "abc123jju2Kbz1POOPonxAkeLG0MctUSdqZADuv1AdSusvNKRSJ"
    val defaultJsonMapper = kotlinx.serialization.json.Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }

    val httpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(defaultJsonMapper)
        }
        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            headers.append("jjkey", key)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 30000
        }
        expectSuccess = false
    }
}
