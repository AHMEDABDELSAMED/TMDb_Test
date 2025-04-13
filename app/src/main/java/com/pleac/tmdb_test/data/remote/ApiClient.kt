package com.pleac.agc.data.remote
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout

object ApiClient {
     val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
         install(HttpTimeout) {
             requestTimeoutMillis = 15000   // max request
             connectTimeoutMillis = 10000   // max try
             socketTimeoutMillis = 15000    // max read
         }
    }


}